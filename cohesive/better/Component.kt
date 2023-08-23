package better

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
    fun isModified(settings: AppSettings): Boolean {
        return settings.getOpenAIKey() != openAIKeyField.text ||
                settings.getAzureToken() != azureTokenField.text
    }

    // 将 settings 数据应用到 UI 
    fun applySettings(settings: AppSettings) {
        openAIKeyField.text = settings.getOpenAIKey()
        azureTokenField.text = settings.getAzureToken()
    }

    fun saveSettings(target: AppSettings) {
        applySettings(target)
    }
}