val lightTheme = Theme(
    name = ThemeName("Light"),
    text = TextConfig(
        color = toColor("#4d4d4d"),
        fontWeight = 400,
    ),
    backgroundColor = toColor("#ffffff"),
    cursorColor = toColor("#527696"),
    project = TextConfig(
        color = toColor("#3C6E71"),
        fontWeight = 700,
    ),
    priority = TextConfig(
        color = toColor("#c16069"),
        fontWeight = 700,
    ),
    context = TextConfig(
        color = toColor("#2ecc71"),
        fontWeight = 700,
    ),
    doneTask = TextConfig(
        color = toColor("#7ea8be"),
        fontWeight = 400,
    ),
    link = TextConfig(
        color = toColor("#0000FF"),
        fontWeight = 400,
    ),
    special = TextConfig(
        color = toColor("#3498db"),
        fontWeight = 700,
    ),
    other = TextConfig(
        color = toColor("#3498db"),
        fontWeight = 700,
    ),
    select = SelectConfig(
        backgroundColor = toColor("#FFEB3B"),
        handleColor = toColor("#000000"),
    ),
    sidebar = SidebarStyle(
        backgroundColor = toColor("#f0f0f0"),
        handleColor = toColor("#b0b0b0"),
        textColor = toColor("#000000"),
    ),
)

val darkTheme = Theme(
    name = ThemeName("Dark"),
    text = TextConfig(
        color = toColor("#F8F8F2"),
        fontWeight = 400,
    ),
    backgroundColor = toColor("#282A36"),
    cursorColor = toColor("#BD93F9"),
    project = TextConfig(
        color = toColor("#FF79C6"),
        fontWeight = 700,
    ),
    priority = TextConfig(
        color = toColor("#8BE9FD"),
        fontWeight = 700,
    ),
    doneTask = TextConfig(
        color = toColor("#6272A4"),
        fontWeight = 400,
    ),
    context = TextConfig(
        color = toColor("#50FA7B"),
        fontWeight = 700,
    ),
    link = TextConfig(
        color = toColor("#FFD700"),
        fontWeight = 400,
    ),
    special = TextConfig(
        color = toColor("#FF9800"),
        fontWeight = 700,
    ),
    other = TextConfig(
        color = toColor("#FF9800"),
        fontWeight = 700,
    ),
    select = SelectConfig(
        backgroundColor = toColor("#BD93F9"),
        handleColor = toColor("#F8F8F2"),
    ),
    sidebar = SidebarStyle(
        backgroundColor = toColor("#1c1c1c"),
        handleColor = toColor("#6272A4"),
        textColor = toColor("#F8F8F0"),
    ),
)
