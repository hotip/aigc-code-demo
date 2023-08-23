package bad

class Configurable: openapi.Configurable {
    private val component = Component(AppSettings.getInstance())
    override fun getPanel(): JComponent = component.panel
    override fun reset() {
        val settings = AppSettings.getInstance()
        component.setOpenAIKey(settings.getOpenAIKey())
        component.setAzureToken(settings.getAzureToken())
        component.setPalmApiKey(settings.getPalmApiKey())
        component.setPalmApiSecret(settings.getPalmApiSecret())
    }
    override fun apply() {
        val settings = AppSettings.getInstance()
        settings.setOpenAIKey(component.getOpenAIKey())
        settings.setAzureToken(component.getAzureToken())
        settings.setPalmApiKey(component.getPalmApiKey())
        settings.setPalmApiSecret(component.getPalmApiSecret())
    }

    override fun isModified(): Boolean {
        val settings = AppSettings.getInstance()
        return settings.getOpenAIKey() != component.getOpenAIKey() ||
                settings.getAzureToken() != component.getAzureToken() ||
                settings.getPalmApiKey() != component.getPalmApiKey() ||
                settings.getPalmApiSecret() != component.getPalmApiSecret()
    }

}