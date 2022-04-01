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
