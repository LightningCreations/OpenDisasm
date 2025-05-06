use std::collections::HashMap;
use std::collections::hash_map::Entry;

use iced::widget::{Column, button, column, container, keyed_column, row, scrollable, text};
use iced::{Element, Length, Task, Theme, window};
use iced_aw::menu::Item;
use iced_aw::{Menu, menu_bar, menu_items};
use opendisasm_core::{Node, NodeBody, NodeRef, add_disassembler_format};
use opendisasm_elf::ElfFormat;
use rfd::AsyncFileDialog;

fn main() -> iced::Result {
    add_disassembler_format(Box::new(ElfFormat));
    iced::application(OpenDisasm::title, OpenDisasm::update, OpenDisasm::view)
        .centered()
        .theme(OpenDisasm::theme)
        .run()
}

#[derive(Default)]
pub struct OpenDisasm {
    root_node: Option<NodeRef>,
    expanded: HashMap<NodeRef, bool>,
    resolved: HashMap<NodeRef, bool>, // not present == not started
}

#[derive(Clone, Debug)]
pub enum Message {
    Null,
    Exit,
    Open,
    SetRoot(NodeRef),
    ResolveNode(NodeRef),
    FinishedResolving(NodeRef),
    ToggleOpen(NodeRef),
}

impl OpenDisasm {
    pub fn title(&self) -> String {
        "OpenDisasm".into()
    }

    pub fn update(&mut self, event: Message) -> Task<Message> {
        match event {
            Message::Null => Task::none(),
            Message::Exit => window::get_latest().and_then(window::close),
            Message::Open => Task::future(async move {
                let file = AsyncFileDialog::new().pick_file().await?;
                Some(Node::from_data(file.read().await))
            })
            .and_then(|root_node| {
                Task::batch([
                    Task::done(Message::SetRoot(root_node)),
                    Task::done(Message::ResolveNode(root_node)),
                ])
            }),
            Message::SetRoot(node) => {
                self.root_node = Some(node);
                Task::none()
            }
            Message::ResolveNode(node) => {
                self.expanded.insert(node, true);
                if let Entry::Vacant(e) = self.resolved.entry(node) {
                    e.insert(false);
                    Task::future(async move {
                        node.get().resolve();
                        Message::FinishedResolving(node)
                    })
                } else {
                    Task::none()
                }
            }
            Message::FinishedResolving(node) => {
                self.resolved.insert(node, true);
                Task::none()
            }
            Message::ToggleOpen(node) => {
                let expand = !*self.expanded.get(&node).unwrap_or(&false);
                if expand {
                    Task::done(Message::ResolveNode(node)) // this will trigger expansion atomically alongside starting resolution
                } else {
                    self.expanded.insert(node, false);
                    Task::none()
                }
            }
        }
    }

    pub fn view(&self) -> Element<Message> {
        let menu_template = |items| Menu::new(items).max_width(180.0).offset(6.0);

        #[rustfmt::skip]
        let mb = menu_bar!(
            (button("File").width(Length::Shrink).on_press(Message::Null), menu_template(menu_items!(
                (button("Open...").width(Length::Fill).on_press(Message::Open))
                (button("Exit").width(Length::Fill).on_press(Message::Exit))
            )))
        );
        container(column![
            mb,
            row![scrollable(if let Some(root_node) = self.root_node {
                self.view_node(root_node)
            } else {
                Element::from(text("Open a file"))
            })]
        ])
        .into()
    }

    fn view_node(&self, node_ref: NodeRef) -> Element<Message> {
        let node = node_ref.get();
        let expanded = *self.expanded.get(&node_ref).unwrap_or(&false);
        let mut title = if let Some(name) = &node.name {
            name.clone()
        } else {
            "<unnamed>".into()
        };
        match node.body() {
            NodeBody::Number(x) => title += &format!(": {x}"),
            _ => {}
        }
        if let Some(x) = &node.context {
            title += &format!(" ({x})");
        }
        let mut result = Column::new().push(if node.expandable() {
            row![
                button(if expanded { "v" } else { ">" }).on_press(Message::ToggleOpen(node_ref)),
                text(title)
            ]
        } else {
            row![text(title)]
        });
        if expanded {
            result = result.push(
                container(if self.resolved.get(&node_ref) == Some(&false) {
                    Element::from(text("Loading...")) // only have to explicitly specify Element once
                } else if let Some(children) = node.children() {
                    keyed_column(children.map(|x| (x, self.view_node(x)))).into()
                } else {
                    text("TODO: automatically close").into()
                })
                .padding([0, 10]),
            );
        }
        result.into()
    }

    pub fn theme(&self) -> Theme {
        Theme::Dark
    }
}
