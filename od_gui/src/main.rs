use iced::{
    button, executor, Application, Button, Clipboard, Column, Command, Container, Element, Length,
    Row, Settings, Text,
};
use od_core::load_file;
use rfd::FileDialog;
use std::fs::File;
use std::path::PathBuf;

static VERSION: &str = "0.1.0";

fn main() -> iced::Result {
    OpenDisasmApplication::run(Settings::default())
}

mod theme {
    use iced::{button, container};

    #[derive(Clone, Copy, Debug)]
    pub enum Theme {
        Dark,
    }

    impl Default for Theme {
        fn default() -> Self {
            Theme::Dark
        }
    }

    impl<'a> From<Theme> for Box<dyn button::StyleSheet + 'a> {
        fn from(t: Theme) -> Self {
            match t {
                Theme::Dark => dark::Button.into(),
            }
        }
    }

    impl<'a> From<Theme> for Box<dyn container::StyleSheet + 'a> {
        fn from(t: Theme) -> Self {
            match t {
                Theme::Dark => dark::Container.into(),
            }
        }
    }

    mod dark {
        use iced::{button, container, Background, Color, Vector};

        pub struct Button;

        impl button::StyleSheet for Button {
            fn active(&self) -> button::Style {
                button::Style {
                    shadow_offset: Vector::new(0.0, 0.0),
                    background: Some(Background::Color(Color::new(0.1, 0.1, 0.1, 1.0))),
                    border_radius: 0.0,
                    border_width: 1.0,
                    border_color: Color::new(0.75, 0.75, 1.0, 1.0),
                    text_color: Color::new(0.75, 0.75, 1.0, 1.0),
                }
            }
        }

        pub struct Container;

        impl container::StyleSheet for Container {
            fn style(&self) -> container::Style {
                container::Style {
                    text_color: Some(Color::new(0.75, 0.75, 1.0, 1.0)),
                    background: Some(Background::Color(Color::new(0.1, 0.1, 0.1, 1.0))),
                    border_radius: 0.0,
                    border_width: 1.0,
                    border_color: Color::new(0.75, 0.75, 1.0, 1.0),
                }
            }
        }
    }
}

#[derive(Clone, Debug)]
enum OdMessage {
    FileOpened(PathBuf),
    Menu(OdMenuId),
    None,
    OpenFile,
    OpenWindow(OdWindowId),
}

#[derive(Copy, Clone, Debug, Eq, Hash, PartialEq)]
enum OdMenuId {
    File,
    Help,
}

#[derive(Clone, Debug)]
struct OdMenu {
    id: OdMenuId,
    items: Vec<(String, OdMessage)>,
}

#[derive(Clone, Debug, Eq, Hash, PartialEq)]
enum OdWindowId {
    About,
    Error(String),
    TreeView,
}

#[derive(Clone, Debug)]
struct OdWindow {
    id: OdWindowId,
}

#[derive(Debug, Default)]
struct OpenDisasmApplication {
    theme: theme::Theme,
    file_menu_state: button::State,
    help_menu_state: button::State,
    open_menu: Option<OdMenu>,
    open_menu_button_states: Vec<button::State>,
    body: Option<OdWindow>, // TODO: Support multiple open windows
}

impl OpenDisasmApplication {
    fn render_window(
        window: &OdWindow,
        _: theme::Theme,
    ) -> Element<<Self as Application>::Message> {
        Text::new(format!("{:?}", window.id)).into()
    }
}

impl Application for OpenDisasmApplication {
    type Executor = executor::Default;
    type Message = OdMessage;
    type Flags = ();

    fn new(_flags: Self::Flags) -> (Self, Command<Self::Message>) {
        (
            OpenDisasmApplication {
                ..Default::default()
            },
            Command::none(),
        )
    }

    fn title(&self) -> String {
        String::from("OpenDisasm v") + VERSION
    }

    fn update(
        &mut self,
        message: Self::Message,
        _clipboard: &mut Clipboard,
    ) -> Command<Self::Message> {
        let mut prev_menu = None;
        std::mem::swap(&mut prev_menu, &mut self.open_menu);
        match message {
            OdMessage::FileOpened(file) => Command::perform(
                async move {
                    let file = File::open(file);
                    if let Ok(file) = file {
                        let _ = load_file(file);
                        OdWindowId::TreeView
                    } else {
                        OdWindowId::Error(String::from("The requested file is unaccessible"))
                    }
                },
                OdMessage::OpenWindow,
            ),
            OdMessage::Menu(id) => {
                if matches!(prev_menu, Some(menu) if menu.id == id) {
                    self.open_menu = None;
                } else {
                    self.open_menu = match id {
                        OdMenuId::File => Some(OdMenu {
                            id,
                            items: vec![(String::from("Open..."), OdMessage::OpenFile)],
                        }),
                        OdMenuId::Help => Some(OdMenu {
                            id,
                            items: vec![(
                                String::from("About"),
                                OdMessage::OpenWindow(OdWindowId::About),
                            )],
                        }),
                    };
                }
                Command::none()
            }
            OdMessage::None => Command::none(),
            OdMessage::OpenFile => {
                let dialog = FileDialog::new();
                Command::perform(async move { dialog.pick_file() }, |out| {
                    out.map_or(OdMessage::None, OdMessage::FileOpened)
                })
            }
            OdMessage::OpenWindow(id) => {
                if !matches!(&self.body, Some(win) if win.id == id) {
                    self.body = Some(OdWindow { id });
                }
                Command::none()
            }
        }
    }

    fn view(&mut self) -> Element<Self::Message> {
        let menu = Container::new(Row::with_children(vec![
            Button::new(&mut self.file_menu_state, Text::new("File"))
                .on_press(OdMessage::Menu(OdMenuId::File))
                .style(self.theme)
                .into(),
            Button::new(&mut self.help_menu_state, Text::new("Help"))
                .on_press(OdMessage::Menu(OdMenuId::Help))
                .style(self.theme)
                .into(),
        ]))
        .style(self.theme)
        .width(Length::Fill);
        let mut menu2 = Row::new();
        if let Some(open_menu) = &self.open_menu {
            if self.open_menu_button_states.len() < open_menu.items.len() {
                for _ in self.open_menu_button_states.len()..open_menu.items.len() {
                    self.open_menu_button_states.push(button::State::default());
                }
            }
            for (item, state) in open_menu
                .items
                .iter()
                .zip(self.open_menu_button_states.iter_mut())
            {
                menu2 = menu2.push(
                    Button::new(state, Text::new(&item.0))
                        .on_press(item.1.clone())
                        .style(self.theme),
                );
            }
        }
        let menu2 = Container::new(menu2).style(self.theme).width(Length::Fill);
        let body = Container::new(match &self.body {
            Some(win) => Self::render_window(win, self.theme),
            None => Row::new().into(),
        })
        .style(self.theme)
        .width(Length::Fill)
        .height(Length::Fill);
        let footer = Container::new(Row::with_children(vec![Text::new(" ").into()]))
            .style(self.theme)
            .width(Length::Fill);
        Container::new(Column::with_children(vec![
            menu.into(),
            menu2.into(),
            body.into(),
            footer.into(),
        ]))
        .style(self.theme)
        .into()
    }
}
