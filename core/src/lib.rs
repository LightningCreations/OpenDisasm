#![deny(clippy::all)]
#![warn(clippy::pedantic, clippy::nursery)]

use aliasable::prelude::*;
use itertools::Either;
use mappable_rc::Marc;

use core::fmt;
use std::{
    collections::HashMap,
    num::NonZero,
    sync::{
        LazyLock,
        atomic::{AtomicUsize, Ordering},
    },
};

use parking_lot::{MappedRwLockReadGuard, RwLock, RwLockReadGuard, RwLockWriteGuard};

static NODES: LazyLock<RwLock<HashMap<NodeRef, AliasableBox<RwLock<Node>>>>> =
    LazyLock::new(|| RwLock::new(HashMap::new()));

static DISASSEMBLER_FORMATS: RwLock<Vec<Box<dyn DisassemblerFormat>>> = RwLock::new(Vec::new());

#[derive(Clone, Copy, Eq, Hash, PartialEq)]
pub struct NodeRef(NonZero<usize>);

impl NodeRef {
    pub fn new(node: Node) -> Self {
        let key = Self::reserve();
        key.insert(node);
        key
    }

    #[allow(clippy::significant_drop_tightening)] // False positive
    fn smuggle_lock<'a>(self) -> Option<&'a RwLock<Node>> {
        let root_lock = NODES.read();
        let data = root_lock.get(&self)?;
        Some(unsafe {
            // Smuggle the reference so root_lock unlocks
            &*(&raw const **data).cast::<RwLock<Node>>()
        })
    }

    pub fn get<'a>(self) -> RwLockReadGuard<'a, Node> {
        self.smuggle_lock().unwrap().read()
    }

    pub fn get_mut<'a>(self) -> RwLockWriteGuard<'a, Node> {
        self.smuggle_lock().unwrap().write()
    }

    pub fn try_get<'a>(self) -> Option<RwLockReadGuard<'a, Node>> {
        self.smuggle_lock().map(|x| x.read())
    }

    #[must_use]
    pub fn reserve() -> Self {
        static COUNTER: AtomicUsize = AtomicUsize::new(1);

        Self(unsafe { NonZero::new(COUNTER.fetch_add(1, Ordering::SeqCst)).unwrap_unchecked() })
    }

    pub fn insert(self, mut node: Node) {
        node.this = self;
        NODES.write().insert(
            self,
            AliasableBox::from_unique(UniqueBox::new(RwLock::new(node))),
        );
    }
}

impl From<Node> for NodeRef {
    fn from(node: Node) -> Self {
        Self::new(node)
    }
}

impl fmt::Debug for NodeRef {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        self.get().fmt(f)
    }
}

#[allow(dead_code)] // I promise `parent` will be useful soon
pub struct Node {
    this: NodeRef,
    parent: Option<NodeRef>,
    pub name: Option<String>,
    pub context: Option<String>,
    body: NodeBody,
}

impl Node {
    #[must_use]
    pub fn from_data(data: Vec<u8>) -> NodeRef {
        NodeBuilder::new().with_data(data).build()
    }

    #[allow(clippy::significant_drop_tightening)] // false positive
    pub fn resolve(&self) {
        if let NodeBody::Data(data) = &self.body {
            let formats = DISASSEMBLER_FORMATS.read();
            let mut recognitions = formats
                .iter()
                .enumerate()
                .flat_map(|(i, x)| {
                    x.recognize(data)
                        .into_iter()
                        .map(|hit| (i, hit))
                        .collect::<Vec<_>>()
                })
                .collect::<Vec<_>>();
            let data: Marc<[u8]> = Marc::from(data.clone());
            recognitions.sort_by(|(_, a), (_, b)| a.start.cmp(&b.start).then(a.len.cmp(&b.len)));
            let nodes = recognitions
                .iter()
                .map(|(i, hit)| {
                    let fmt = &formats[*i];
                    NodeBuilder::new()
                        .with_name(format!("{} @ {:#010X}", fmt.format_name(), hit.start))
                        .with_partial(fmt.go(Marc::map(data.clone(), |x| {
                            hit.len
                                .map_or(&x[hit.start..], |len| &x[hit.start..(hit.start + len)])
                        })))
                        .build()
                })
                .collect::<Vec<_>>();
            // TODO: handle supercedes
            let builder = NodeBuilder::in_place(self.this);
            let builder = if let Some(name) = self.name.clone() {
                builder.with_name(name)
            } else {
                builder
            };
            let _ = builder.with_tree(nodes).build(); // we only care to insert the node
        }

        if let NodeBody::Partial(data) = &self.body {
            data.write().cont(self.this);
        }
    }

    pub const fn expandable(&self) -> bool {
        matches!(
            self.body,
            NodeBody::Data(_) | NodeBody::Partial(_) | NodeBody::Array(_) | NodeBody::Tree(_)
        )
    }

    pub fn children(&self) -> Option<impl Iterator<Item = NodeRef>> {
        if let NodeBody::Array(body) = &self.body {
            Some(Either::Left(body.iter().copied()))
        } else if let NodeBody::Tree(body) = &self.body {
            Some(Either::Right(body.iter().map(|x| x.1)))
        } else {
            None
        }
    }

    pub fn body(&self) -> &NodeBody {
        &self.body
    }
}

impl fmt::Debug for Node {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        self.body.fmt(f)
    }
}

pub enum NodeBody {
    Data(Vec<u8>),
    Partial(RwLock<Box<dyn DisassemblerState>>),
    Array(Vec<NodeRef>),
    Tree(Vec<(String, NodeRef)>),
    Number(u64),
}

impl fmt::Debug for NodeBody {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        match &self {
            Self::Data(_) => write!(f, "Data(...)"),
            Self::Partial(_) => write!(f, "Partial(...)"),
            Self::Array(data) => f.debug_tuple("Array").field(data).finish(),
            Self::Tree(data) => f.debug_tuple("Tree").field(data).finish(),
            Self::Number(data) => f.debug_tuple("Number").field(data).finish(),
        }
    }
}

pub struct NodeBuilder<T = ()> {
    node: NodeRef,
    name: Option<String>,
    context: Option<String>,
    data: T,
}

pub struct DataNodeData(Vec<u8>);
pub struct PartialNodeData(Box<dyn DisassemblerState>);
#[allow(dead_code)]
pub struct ArrayNodeData(Vec<NodeRef>);
pub struct TreeNodeData(Vec<NodeRef>);
pub struct NumberNodeData(u64);

impl<T: Default> NodeBuilder<T> {
    #[must_use]
    pub fn new() -> Self {
        Self::default()
    }

    #[must_use]
    pub fn in_place(node: NodeRef) -> Self {
        let name = node.try_get().and_then(|x| x.name.clone());
        let context = node.try_get().and_then(|x| x.context.clone());
        Self {
            node,
            data: T::default(),
            name,
            context,
        }
    }
}

impl<T> NodeBuilder<T> {
    #[allow(clippy::needless_pass_by_value)] // ergonomics
    #[must_use]
    pub fn with_name(mut self, name: impl ToString) -> Self {
        self.name = Some(name.to_string());
        self
    }

    #[allow(clippy::needless_pass_by_value)] // ergonomics
    #[must_use]
    pub fn with_context(mut self, context: impl ToString) -> Self {
        self.context = Some(context.to_string());
        self
    }
}

impl NodeBuilder<()> {
    #[must_use]
    pub fn with_data(self, data: Vec<u8>) -> NodeBuilder<DataNodeData> {
        NodeBuilder {
            node: self.node,
            data: DataNodeData(data),
            name: self.name,
            context: self.context,
        }
    }

    #[must_use]
    pub fn with_partial(self, data: Box<dyn DisassemblerState>) -> NodeBuilder<PartialNodeData> {
        NodeBuilder {
            node: self.node,
            data: PartialNodeData(data),
            name: self.name,
            context: self.context,
        }
    }

    #[must_use]
    pub fn with_array(self, data: Vec<NodeRef>) -> NodeBuilder<ArrayNodeData> {
        NodeBuilder {
            node: self.node,
            data: ArrayNodeData(data),
            name: self.name,
            context: self.context,
        }
    }

    #[must_use]
    pub fn with_tree(self, data: Vec<NodeRef>) -> NodeBuilder<TreeNodeData> {
        NodeBuilder {
            node: self.node,
            data: TreeNodeData(data),
            name: self.name,
            context: self.context,
        }
    }

    #[must_use]
    pub fn with_number(self, data: impl Into<u64>) -> NodeBuilder<NumberNodeData> {
        NodeBuilder {
            node: self.node,
            data: NumberNodeData(data.into()),
            name: self.name,
            context: self.context,
        }
    }
}

impl NodeBuilder<DataNodeData> {
    #[must_use]
    pub fn build(self) -> NodeRef {
        self.node.insert(Node {
            this: self.node,
            parent: None,
            name: self.name,
            context: self.context,
            body: NodeBody::Data(self.data.0),
        });
        self.node
    }
}

impl NodeBuilder<PartialNodeData> {
    #[must_use]
    pub fn build(self) -> NodeRef {
        self.node.insert(Node {
            this: self.node,
            parent: None,
            name: self.name,
            context: self.context,
            body: NodeBody::Partial(RwLock::new(self.data.0)),
        });
        self.node
    }
}

impl NodeBuilder<TreeNodeData> {
    #[must_use]
    pub fn build(self) -> NodeRef {
        self.node.insert(Node {
            this: self.node,
            parent: None,
            name: self.name,
            context: self.context,
            body: NodeBody::Tree(
                self.data
                    .0
                    .into_iter()
                    .map(|x| {
                        (
                            x.get()
                                .name
                                .as_ref()
                                .map_or_else(String::new, String::clone),
                            x,
                        )
                    })
                    .collect(),
            ),
        });
        self.node
    }
}

impl NodeBuilder<NumberNodeData> {
    #[must_use]
    pub fn build(self) -> NodeRef {
        self.node.insert(Node {
            this: self.node,
            parent: None,
            name: self.name,
            context: self.context,
            body: NodeBody::Number(self.data.0),
        });
        self.node
    }
}

impl<T: Default> Default for NodeBuilder<T> {
    fn default() -> Self {
        // SAFETY: when the node is built, before returning the `NodeRef`, the node will
        // be inserted.
        Self::in_place(NodeRef::reserve())
    }
}

pub struct RecognitionHit {
    pub start: usize,
    pub len: Option<usize>,
}

pub trait DisassemblerFormat: Send + Sync {
    fn format_name(&self) -> &'static str;
    fn recognize(&self, data: &[u8]) -> Vec<RecognitionHit>;
    fn supercedes(&self, other: &'static str) -> bool;
    fn go(&self, data: Marc<[u8]>) -> Box<dyn DisassemblerState>;
}

pub trait DisassemblerState: fmt::Debug + Send + Sync {
    /// Continue disassembly at this `Node`.
    ///
    /// Returns `Some` when the disassembly has completely finished.
    /// May return `None` if disassembly isn't finished yet or if disassembly
    /// *has* finished and the state is now empty.
    fn cont(&mut self, node: NodeRef);
}

pub fn add_disassembler_format(format: Box<dyn DisassemblerFormat>) {
    DISASSEMBLER_FORMATS.write().push(format);
}

pub fn disassembler_for_format(
    format: &str,
) -> Option<MappedRwLockReadGuard<dyn DisassemblerFormat>> {
    RwLockReadGuard::try_map(DISASSEMBLER_FORMATS.read(), |fmts| {
        fmts.iter()
            .find(|x| x.format_name() == format)
            .map(|x| &**x)
    })
    .ok()
}
