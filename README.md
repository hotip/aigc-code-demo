在 [[AI-Programming]]一文中，我做过推测：

> - 需求文档规范化 + LLM 可大大提升生成代码的匹配程度
> - 使用文档规范化 + LLM 可大大提升文档查询效率（不管提供给人类查询，还是提供给 AI 做训练集）
> - 人类的主要工作：生产新的库 + 规范化文档（而这两项工作可能大部分时间都是和 AI 协作，主要负责写需求和修改 AI 生成的内容）
> -  不规范的遗留项目里，AI 较难直接发挥太大作用，因为问题是系统性的。最终工程效率可能会被规范化的项目远远抛在后面。

其中最后一条，**不规范的遗留项目中，AI 较难发挥太大作用**，这几天开发和使用 AI 的经历一定程度上印证了这一条。这里记录一下。

## 背景

开源项目：[一个 JetBrains IDEA AI 辅助插件](https://github.com/unit-mesh/auto-dev)
涉及文件：[AppSettingsCompont.kt](https://github.com/unit-mesh/auto-dev/blob/764554033fbea912bd8116a5cf2de8e15aea021f/src/main/kotlin/cc/unitmesh/devti/settings/AppSettingsComponent.kt#L4) / [AutoDevSettingConfigurable.kt](https://github.com/unit-mesh/auto-dev/blob/764554033fbea912bd8116a5cf2de8e15aea021f/src/main/kotlin/cc/unitmesh/devti/settings/AutoDevSettingsConfigurable.kt)
文件简价：`Configurable` 是 IDEA 插件设置页面相关配置入口类，一般其对应的 UI 可直接在里面写，也可另起一个类专门管理 UI，此项目用的是后者。
问题：
- AppSettingsComponent 暴露过多方法给外部，尽管把 UI 组件这个细节封起来了，但依然是个 bad smell。
```kotlin
    fun getOpenAiKey(): String {
        return openAiKey.text
    }

    fun setOpenAiKey(newText: String) {
        openAiKey.text = newText
    }

    fun getGithubToken(): String {
        return githubToken.text
    }

    fun setGithubToken(newText: String) {
        githubToken.text = newText
    }

    fun getOpenAiModel(): String {
        return openAiModel.selectedItem?.toString() ?: OPENAI_MODEL[0]
    }
    // 略，每个配置项都有一个 setter / getter，功能为设置 UI 组件的值及获取其值
```
- 职责不够内聚，要增加一个设置，需要同时在 Configurable 修改两个函数以及在 SettingCompoent 上增加一个成员，三个函数，修改一个函数。(当然，还要修改数据类，但这部分独立是合理的，这里略过)
```kotlin
// Configurable 类
fun reset(){
	val settings: AutoDevSettingsState = AutoDevSettingsState.getInstance()
    component.setLanguage(settings.language)
    // ... 其他 setter
    // 新加的 setter 调用
}

fun apply() {
	val settings: AutoDevSettingsState = AutoDevSettingsState.getInstance()
    settings.openAiKey = component.getOpenAiKey()
    // 从其他 getter 获取设置应用到最终数据存储对象 settings 中
    // 对新加属性调用 setter
}

// Compoent 类
private val newParamCompoent = JBTextField()
fun setNewParam(newText: String) { ... }
fun getNewParam(): String { ... }
fun isModified(): Boolean { 
    // 比较 UI 数据和原始数据是否有变以让 IDEA 决定是否出现 `重置` 和 `应用` 按钮
    // 添加新配置的比较逻辑
}
// UI addChild 部分略
```

解决[这个内聚问题](https://github.com/unit-mesh/auto-dev/commit/54a01cc5c206ae8edd65e6803f5243075c4231f0)，`AppComponent` 负责封装所有和数据类有关的操作，包括：
- 界面初始化为所输入的 settings 数据类，
- 当界面变化时，`isModified` 函数返回 true 
- 提供接口导出当前 UI 所呈现的数据
主要修改也很简单，就是**把 configurable 类的 reset / apply 方法所做的逻辑全移到 component**（ 以及一些 public 变为 private 之类的小重构）。

如此，当需要增加一个配置项时，除了数据类及数据类实例化以外的工作，全都在这个 Component 里了。这个改动其实就是单一职责原则的一个表述：有且只有一个理由修改一个类。这个理由是：**把新加的配置应用到设置UI 上**。

## 价值

很多时候，我们很难去清楚地说明这类重构的价值在哪里，因为不管 reset/apply 这两个函数放在哪，对维护者来说工作量变化都不大，无外乎点多两个文件切换。

但是，在 AI 时代这可能就比较有意义了。前面的描述可能不太清晰，为了方便试验。这里构建了一个新仓库演示:

[aigc-code-demo](https://github.com/hotip/aigc-code-demo)

Bad Case (一个类一个文件，这里为方便放一起了)
```kotlin
class Configurable: openapi.Configurable {
    private val component = Component(AppSettings.getInstance())
    override fun getPanel(): JComponent = component.panel
    override fun reset() {
        val settings = AppSettings.getInstance()
        component.setOpenAIKey(settings.getOpenAIKey())
        component.setAzureToken(settings.getAzureToken())
    }
    override fun apply() {
        val settings = AppSettings.getInstance()
        settings.setOpenAIKey(component.getOpenAIKey())
        settings.setAzureToken(component.getAzureToken())
    }

    override fun isModified(): Boolean {
        val settings = AppSettings.getInstance()
        return settings.getOpenAIKey() != component.getOpenAIKey() ||
                settings.getAzureToken() != component.getAzureToken()
    }

}

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
```
Good Case
```kotlin
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
```

现在我们的需求是，工具要求增加对 PaLM 的支持，而 PaLM 需要增加两个配置 apiKey 和 apiSecect，调用端已经把数据类 `AppSetting` 写好。现在需要给设置项加上可配置 UI 入口。

我们尝试用 Copilot 生成， Componet 文件添加