use eframe::{egui, epi};
use od_core::structure::{IntClass, Leaf, NodeState, TreeNode};
use std::fs::File;
use std::ops::Deref;
use std::sync::{Arc, RwLock};

#[derive(Default)]
struct OpenDisasmApp {
    about_window_open: bool,
    tree: Arc<RwLock<Option<TreeNode>>>,
}

impl OpenDisasmApp {
    fn render_node(tree: &TreeNode, name: &str, ui: &mut egui::Ui) {
        match &tree.state {
            NodeState::Leaf { typename, value } => {
                ui.label(format!(
                    "{}: {} ({})",
                    name,
                    typename,
                    match value {
                        Leaf::Enum {
                            val,
                            names,
                        } => format!("{} = {}", val, names[val]),
                        Leaf::Int {
                            val,
                            class: IntClass::Unsigned,
                            ..
                        } => format!("{}", val),
                        x => todo!("{:?}", x),
                    }
                ));
            }
            NodeState::List { typename, value } => {
                ui.collapsing(format!("{}: {}", name, typename), |ui| {
                    for (i, node) in value.iter().enumerate() {
                        Self::render_node(node, &format!("{}", i), ui);
                    }
                });
            }
            NodeState::Object {
                typename,
                value,
                order,
            } => {
                ui.collapsing(format!("{}: {}", name, typename), |ui| {
                    for field in order {
                        Self::render_node(value.get(field).unwrap(), field, ui);
                    }
                });
            }
            x => todo!("{:?}", x),
        }
    }
}

impl epi::App for OpenDisasmApp {
    fn name(&self) -> &str {
        "OpenDisasm"
    }

    fn update(&mut self, ctx: &egui::Context, frame: &epi::Frame) {
        egui::TopBottomPanel::top("file_menu").show(ctx, |ui| {
            ui.horizontal(|ui| {
                ui.collapsing("File", |ui| {
                    if ui.button("Open").clicked() {
                        tokio::spawn({
                            let tree = self.tree.clone();
                            async move {
                                if let Some(file) = rfd::AsyncFileDialog::new().pick_file().await {
                                    let result = File::open(file.path())
                                        .ok()
                                        .and_then(|x| od_core::load_file(x).ok());
                                    *tree.write().unwrap() = result;
                                }
                            }
                        });
                    }
                    if ui.button("Exit").clicked() {
                        frame.quit();
                    }
                });
                ui.collapsing("Help", |ui| {
                    if ui.button("About").clicked() {
                        self.about_window_open = true;
                    }
                });
            });
        });
        egui::CentralPanel::default().show(ctx, |ui| {
            if let Some(tree) = self.tree.read().unwrap().deref() {
                Self::render_node(tree, "<root>", ui);
            }
        });
        egui::Window::new("About").open(&mut self.about_window_open).show(ctx, |ui| {
            ui.label("OpenDisasm");
            ui.label("Released under the BSD-Plus-Patent license; for more information, read the LICENSE file.");
            ui.label("© 2022 Lightning Creations");
        });
    }
}

#[tokio::main]
async fn main() {
    let app = OpenDisasmApp::default();
    let native_options = eframe::NativeOptions::default();
    eframe::run_native(Box::new(app), native_options);
}
