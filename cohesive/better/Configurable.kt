package better

class Configurable: openapi.Configurable {
    private val component = Component(AppSettings.getInstance())
    override fun getPanel(): JComponent = component.panel
    override fun reset() {
        component.applySettings(AppSettings.getInstance())
    }
    override fun apply() {
        component.saveSettings(target = AppSettings.getInstance())
    }

    override fun isModified(): Boolean {
        return component.isModified(AppSettings.getInstance())
    }

}