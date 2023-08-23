package bad

class Component(settings: AppSettings) {
    val panel = JPanel()
    private val openAIKeyField = JTextField(settings.getOpenAIKey())
    private val azureTokenField = JTextField(settings.getAzureToken())
    private val palmApiKey = JTextField(settings.getPalmApiKey())
    private val palmApiSecrect = JTextField(settings.getPalmApiSecret())

    init {
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.add(JLabel("OpenAI Key"))
        panel.add(openAIKeyField)
        panel.add(JLabel("Azure Token"))
        panel.add(azureTokenField)
        panel.add(JLabel("Palm API Key"))
        panel.add(palmApiKey)
    }

    fun setPalmApiKey(key: String) {
        palmApiKey.text = key
    }

    fun getPalmApiKey(): String = palmApiKey.text

    fun setPalmApiSecret(secret: String) {
        palmApiSecrect.text = secret
    }

    fun getPalmApiSecret(): String = palmApiSecrect.text

    fun setOpenAIKey(key: String) {
        openAIKeyField.text = key
    }
    fun setAzureToken(token: String) {
        azureTokenField.text = token
    }
    fun getOpenAIKey(): String = openAIKeyField.text
    fun getAzureToken(): String = azureTokenField.text
}