use od_core::structure::{NodeId, NodeState, TreeNode};

use slint::VecModel;

use std::collections::HashSet;
use std::rc::Rc;

slint::include_modules!();

fn generate_visual_tree(
    tree: &TreeNode,
    expanded: &HashSet<NodeId>,
    indent: i32,
    name: String,
) -> Vec<VisualTreeNode> {
    let mut result = Vec::new();
    let expanded = expanded.contains(&tree.id);
    result.push(VisualTreeNode {
        expanded,
        id: tree.id.0 as i32,
        indent,
        name: name.into(),
    });
    result
}

fn main() {
    let main = Main::new();
    let mut expanded = HashSet::new();
    let tree = None;

    let visual_tree = Rc::new(VecModel::from(if let Some(tree) = &tree {
        generate_visual_tree(tree, &expanded, 0, String::from("<root>"))
    } else {
        Vec::new()
    }));

    let menu_items = Rc::new(VecModel::from(vec![
        MenuItem {
            name: "File".into(),
            children: Rc::new(VecModel::from(vec![
                SubMenuItem {
                    name: "Open".into(),
                },
            ])).into(),
        },
        MenuItem {
            name: "Help".into(),
            children: Rc::new(VecModel::default()).into(),
        },
    ]));

    main.on_expand({
        let visual_tree = visual_tree.clone();
        move |id, expand| {
            if expand {
                expanded.insert(NodeId(id as u64));
            } else {
                expanded.remove(&NodeId(id as u64));
            }
            if let Some(tree) = &tree {
                visual_tree.set_vec(generate_visual_tree(
                    tree,
                    &expanded,
                    0,
                    String::from("<root>"),
                ));
            } else {
                visual_tree.set_vec(Vec::new());
            }
        }
    });

    main.set_tree(visual_tree.into());
    main.set_menu_items(menu_items.into());
    main.run();
}
