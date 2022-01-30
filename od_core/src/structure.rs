use xlang_abi::prelude::v1::*;

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
pub struct NodeId(u64);

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

#[derive(Clone, Debug)]
#[repr(C)]
pub struct TreeNode {
    state: NodeState,
    disasm_id: String,
    format: Option<String>,
    id: NodeId,
    has_incomplete_children: bool,
}