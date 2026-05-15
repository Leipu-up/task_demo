package com.example.demo.test;

import org.htmlunit.WebClient;
import org.htmlunit.html.*;

import java.util.List;

public class FormAutoFillDemoW3C {
	public static void main(String[] args) {
		try (WebClient webClient = new WebClient()) {
			// 配置 WebClient
			configureWebClient(webClient);

			System.out.println("正在访问 W3School 示例页面...");
			HtmlPage page = webClient.getPage("https://www.w3schools.com/html/html_forms.asp");
			System.out.println("页面标题: " + page.getTitleText());

			// 获取页面中的所有表单
			List<HtmlForm> forms = page.getForms();
			System.out.println("找到 " + forms.size() + " 个表单");

			if (forms.isEmpty()) {
				System.out.println("未找到任何表单，尝试直接查找 input 元素...");
				fillInputsDirectly(page);
			} else {
				// 使用第一个表单
				HtmlForm form = forms.get(0);
				System.out.println("使用表单: " + form.getNameAttribute());

				// 智能填充所有文本输入框
				fillAllTextInputs(form);
			}

		} catch (Exception e) {
			System.err.println("执行出错: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 智能填充表单中的所有文本输入框（修改后的核心方法）
	 */
	private static void fillAllTextInputs(HtmlForm form) {
		// 修改点1：使用 getByXPath 替代 getHtmlElementsByTagName
		List<HtmlInput> inputs = form.getByXPath(".//input");

		System.out.println("通过 XPath 找到了 " + inputs.size() + " 个 input 元素");

		HtmlInput submitButton = null;  // 修改：使用 HtmlInput 而不是 HtmlSubmitInput

		for (HtmlInput input : inputs) {
			String type = input.getTypeAttribute();
			String name = input.getNameAttribute();
			String id = input.getId();

			System.out.println("发现输入框 - 类型: " + type + ", name: " + name + ", id: " + id);

			switch (type) {
				case "text":
				case "email":
				case "tel":
				case "search":
					try {
						input.type("测试内容_" + (name.isEmpty() ? id : name));
						System.out.println("  ✓ 已填写文本输入框: " + (name.isEmpty() ? id : name));
					} catch (Exception e) {
						System.err.println("  ✗ 填写文本输入框失败: " + e.getMessage());
					}
					break;

				case "password":
					try {
						input.type("test123");
						System.out.println("  ✓ 已填写密码框: " + name);
					} catch (Exception e) {
						System.err.println("  ✗ 填写密码框失败: " + e.getMessage());
					}
					break;

				case "submit":
					System.out.println("  - 发现提交按钮: " + name);
					submitButton = input;  // 修改：直接赋值，不需要类型转换
					break;

				case "checkbox":
					try {
						input.setChecked(true);
						System.out.println("  ✓ 已勾选复选框: " + name);
					} catch (Exception e) {
						System.err.println("  ✗ 勾选复选框失败: " + e.getMessage());
					}
					break;

				case "radio":
					try {
						input.setChecked(true);
						System.out.println("  ✓ 已选中单选框: " + name);
					} catch (Exception e) {
						System.err.println("  ✗ 选中单选框失败: " + e.getMessage());
					}
					break;

				default:
					System.out.println("  - 未处理的输入类型: " + type + ", name: " + name);
			}
		}

		// 修改点2：使用 XPath 查找按钮（如果上面没找到）
		if (submitButton == null) {
			submitButton = findSubmitButton(form);
		}

		// 提交表单
		if (submitButton != null) {
			try {
				System.out.println("正在提交表单...");
				HtmlPage resultPage = submitButton.click();
				System.out.println("提交后页面标题: " + resultPage.getTitleText());
				System.out.println("✓ 表单提交成功！");
			} catch (Exception e) {
				System.out.println("提交失败: " + e.getMessage());
			}
		} else {
			System.out.println("未找到提交按钮，尝试其他方式提交...");
		}
	}

	/**
	 * 查找提交按钮（多种方式）
	 * 修改：返回类型改为 HtmlInput
	 */
	private static HtmlInput findSubmitButton(HtmlForm form) {
		// 方式1：通过 XPath 查找 type=submit 的 input
		List<HtmlInput> submitInputs = form.getByXPath(".//input[@type='submit']");
		if (!submitInputs.isEmpty()) {
			System.out.println("通过 XPath 找到提交按钮");
			return submitInputs.get(0);
		}

		/*// 方式2：查找 button 类型为 submit 的元素
		List<HtmlButton> buttons = form.getByXPath(".//button[@type='submit']");
		if (!buttons.isEmpty()) {
			System.out.println("通过 XPath 找到 button 类型的提交按钮");
			return buttons.get(0);  // 直接返回 HtmlButton，它可以调用 click()
		}

		// 方式3：查找所有 button 元素
		List<HtmlButton> allButtons = form.getByXPath(".//button");
		for (HtmlButton button : allButtons) {
			String text = button.asNormalizedText();
			if (text.contains("提交") || text.contains("登录") || text.contains("Submit") || text.contains("Login")) {
				System.out.println("通过按钮文本找到提交按钮: " + text);
				return button;
			}
		}*/

		return null;
	}


	/**
	 * 直接填充页面上的所有输入框（当没有表单时使用）
	 */
	private static void fillInputsDirectly(HtmlPage page) {
		// 修改点3：使用 XPath 直接查找页面上的所有输入框
		List<HtmlInput> inputs = page.getByXPath("//input[@type='text' or @type='password' or @type='email']");
		System.out.println("找到 " + inputs.size() + " 个输入框");

		for (HtmlInput input : inputs) {
			String type = input.getTypeAttribute();
			String name = input.getNameAttribute();

			try {
				if ("text".equals(type) || "email".equals(type)) {
					input.type("测试数据_" + (name.isEmpty() ? "未知字段" : name));
					System.out.println("✓ 已填写: " + (name.isEmpty() ? "未命名文本框" : name));
				} else if ("password".equals(type)) {
					input.type("password123");
					System.out.println("✓ 已填写密码框: " + name);
				}
			} catch (Exception e) {
				System.err.println("✗ 填写失败: " + e.getMessage());
			}
		}

		// 尝试找到并点击提交按钮
		List<HtmlInput> submitButtons = page.getByXPath("//input[@type='submit']");
		if (!submitButtons.isEmpty()) {
			try {
				System.out.println("找到提交按钮，正在提交...");
				HtmlPage resultPage = submitButtons.get(0).click();
				System.out.println("提交后页面标题: " + resultPage.getTitleText());
			} catch (Exception e) {
				System.err.println("点击提交按钮失败: " + e.getMessage());
			}
		}
	}

	/**
	 * 配置 WebClient
	 */
	private static void configureWebClient(WebClient webClient) {
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setUseInsecureSSL(true);
		webClient.getOptions().setTimeout(30000);

		// 忽略 HTTP 状态码错误（如 404）
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

		// 设置 User-Agent 模拟真实浏览器
		webClient.addRequestHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

		System.out.println("WebClient 配置完成");
	}
}
