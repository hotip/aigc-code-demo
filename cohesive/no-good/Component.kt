class Component(settings: AppSettings) {
    val panel = JPanel()
    private val openAIKeyField = JTextField(settings.getOpenAIKey())
    private val azureTokenField = JTextField(settings.getAzureToken())
    init {
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.add(JLabel("OpenAI Key"))
        panel.add(openAIKeyField)
        panel.add(JLabel("Azure Token"))
        panel.add(azureTokenField)
    }

    fun setOpenAIKey(key: String) {
        openAIKeyField.text = key
    }
    fun setAzureToken(token: String) {
        azureTokenField.text = token
    }
    fun getOpenAIKey(): String = openAIKeyField.text
    fun getAzureToken(): String = azureTokenField.text
}