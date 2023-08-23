package better

class Component(settings: AppSettings) {
    val panel = JPanel()
    private val openAIKeyField = JTextField(settings.getOpenAIKey())
    private val azureTokenField = JTextField(settings.getAzureToken())
    private val palmApiKeyField = JTextField(settings.getpalmApiKey())
    private val palmApiSecretField = JTextField(settings.getpalmApiSecret())

    init {
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.add(JLabel("OpenAI Key"))
        panel.add(openAIKeyField)
        panel.add(JLabel("Azure Token"))
        panel.add(azureTokenField)
        panel.add(JLabel("Azure API Key"))
        panel.add(palmApiKeyField)
        panel.add(JLabel("Azure API Secret"))
        panel.add(palmApiSecretField)
    }
    fun isModified(settings: AppSettings): Boolean {
        return settings.getOpenAIKey() != openAIKeyField.text ||
                settings.getAzureToken() != azureTokenField.text ||
                settings.getpalmApiKey() != palmApiKeyField.text ||
                settings.getpalmApiSecret() != palmApiSecretField.text
    }

    // 将 settings 数据应用到 UI 
    fun applySettings(settings: AppSettings) {
        openAIKeyField.text = settings.getOpenAIKey()
        azureTokenField.text = settings.getAzureToken()
        palmApiKeyField.text = settings.getpalmApiKey()
        palmApiSecretField.text = settings.getpalmApiSecret()
    }

    fun saveSettings(target: AppSettings) {
        applySettings(target)
    }
}