use xlang_abi::prelude::v1::*;

use std::sync::atomic::{AtomicU64, Ordering};

#[derive(Clone, Copy, Debug, Eq, Hash, PartialEq)]
#[repr(u8)]
pub enum IntClass {
    Boolean,
    Character,
    Signed,
    Unsigned,
}

#[derive(Clone, Debug, Hash, PartialEq)]
#[repr(u8)]
pub enum Leaf {
    Enum {
        val: u128,
        names: HashMap<u128, String>,
    },
    ExtInt {
        val: Vec<u8>,
        size: u16,
        class: IntClass,
    },
    FileRef {
        pos: FilePosition,
        len: Option<u64>,
    },
    Int {
        val: u128,
        size: u8,
        class: IntClass,
    },
    NodeRef {
        id: NodeId,
        pos: Option<u64>,
        len: Option<u64>,
    },
    Opcode {
        val: Vec<u8>,
        name: String,
    },
}

impl Leaf {
    pub fn new_enum(val: u128, names: HashMap<u128, String>) -> Self {
        Leaf::Enum { val, names }
    }
}

#[derive(Clone, Copy, Debug, Eq, Hash, PartialEq)]
#[repr(C)]
pub struct FilePosition {
    id: u64,
    position: u64,
}

#[derive(Clone, Copy, Debug, Eq, Hash, PartialEq)]
#[repr(transparent)]
pub struct NodeId(pub u64);

static NEXT_ID: AtomicU64 = AtomicU64::new(1);

impl NodeId {
    pub fn new() -> Self {
        NodeId(NEXT_ID.fetch_add(1, Ordering::Relaxed))
    }
}

impl Default for NodeId {
    fn default() -> Self {
        Self::new()
    }
}

#[derive(Clone, Debug)]
#[repr(u8)]
pub enum NodeState {
    Object {
        typename: String,
        value: HashMap<String, TreeNode>,
        order: Vec<String>,
    },
    List {
        typename: String,
        value: Vec<TreeNode>,
    },
    Leaf {
        typename: String,
        value: Leaf,
    },
    Array {
        typename: String,
        value: Vec<Leaf>,
    },
    InstructionList {
        arch: String,
        value: Vec<TreeNode>,
    },
    Instruction {
        opcode: Box<TreeNode>,
        operands: Vec<TreeNode>,
    },
    Incomplete {
        waiting_on: Vec<String>,
        head_id: NodeId,
        index_in_file: FilePosition,
    },
}

impl NodeState {
    pub fn new_enum<S: AsRef<str> + ?Sized>(
        typename: &S,
        val: u128,
        names: HashMap<u128, String>,
    ) -> Self {
        Self::Leaf {
            typename: typename.into(),
            value: Leaf::new_enum(val, names),
        }
    }
}

impl From<char> for Leaf {
    fn from(val: char) -> Self {
        Self::Int {
            val: val as u128,
            size: 32,
            class: IntClass::Character,
        }
    }
}

impl From<char> for NodeState {
    fn from(val: char) -> Self {
        Self::Leaf {
            typename: "char".into(),
            value: val.into(),
        }
    }
}

impl From<u8> for NodeState {
    fn from(val: u8) -> Self {
        Self::Leaf {
            typename: String::from("u8"),
            value: Leaf::Int {
                val: val as u128,
                size: 8,
                class: IntClass::Unsigned,
            },
        }
    }
}

impl From<u16> for NodeState {
    fn from(val: u16) -> Self {
        Self::Leaf {
            typename: String::from("u16"),
            value: Leaf::Int {
                val: val as u128,
                size: 16,
                class: IntClass::Unsigned,
            },
        }
    }
}

impl From<u32> for NodeState {
    fn from(val: u32) -> Self {
        Self::Leaf {
            typename: String::from("u32"),
            value: Leaf::Int {
                val: val as u128,
                size: 32,
                class: IntClass::Unsigned,
            },
        }
    }
}

impl From<u64> for NodeState {
    fn from(val: u64) -> Self {
        Self::Leaf {
            typename: String::from("u64"),
            value: Leaf::Int {
                val: val as u128,
                size: 64,
                class: IntClass::Unsigned,
            },
        }
    }
}

impl From<&str> for NodeState {
    fn from(val: &str) -> Self {
        Self::Array {
            typename: "str".into(),
            value: val.chars().map(Leaf::from).collect(),
        }
    }
}

// TODO: change `pub` to accessors
#[derive(Clone, Debug)]
#[repr(C)]
pub struct TreeNode {
    pub state: NodeState,
    pub disasm_id: String,
    pub format: Option<String>,
    pub id: NodeId,
    pub has_incomplete_children: bool,
}

impl TreeNode {
    pub fn empty(disasm_id: String) -> Self {
        Self {
            state: NodeState::List {
                typename: String::from("<none>"),
                value: Vec::new(),
            },
            disasm_id,
            format: None,
            id: NodeId::new(),
            has_incomplete_children: false,
        }
    }
}

impl Default for TreeNode {
    fn default() -> Self {
        Self::empty(String::from("<none>"))
    }
}
