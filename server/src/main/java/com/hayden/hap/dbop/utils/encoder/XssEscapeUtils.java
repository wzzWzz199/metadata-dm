package com.hayden.hap.dbop.utils.encoder;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.regex.Pattern;

/**
 * 
 * @author zhangfeng
 * @date 2018年9月28日
 */
public class XssEscapeUtils {
	
	public static String encode(String s) {
		if(s==null) {
			return s;
		}
		s = xssEncode(s, null);
		s = HTMLEncode(s);
		return s;
	}

	/**
	 * 对一些特殊字符进行转义
	 */
	public static String HTMLEncode(String aText) {

		final StringBuilder result = new StringBuilder();

		final StringCharacterIterator iterator = new StringCharacterIterator(aText);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {

			if (character == '<') {
				result.append("<");
			} else if (character == '>') {
				result.append(">");
			} else if (character == '&') {
				result.append("&");
			} else if (character == '\"') {
				result.append("\"");
			} else {
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}


	/**
	 * 将容易引起xss漏洞的半角字符直接替换成全角字符
	 * 目前xssProject对注入代码要求是必须开始标签和结束标签(如<script></script>)正确匹配才能解析，否则报错；因此只能替换调xssProject换为自定义实现
	 * 
	 * @param s
	 * @return
	 */

	private static String xssEncode(String s) {
		return xssEncode(s, null);
	}

	private static String xssEncode(String s, String name) {
		if (s == null || s.isEmpty()) {
			return s;
		}
		String result = stripXSS(s, name);
		return result;
	}

	public static String escape(String s) {

		StringBuilder sb = new StringBuilder(s.length() + 16);
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '>':
				sb.append('＞');// 全角大于号
				break;
			case '<':
				sb.append('＜');// 全角小于号
				break;
			case '\'':
				sb.append('‘');// 全角单引号
				break;
			case '\"':
				sb.append('“');// 全角双引号
				break;
			case '\\':
				sb.append('＼');// 全角斜线
				break;
			case '%':
				sb.append('％'); // 全角冒号
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

	private static String stripXSS(String value, String name) {
		if (value != null) {
			// NOTE: It's highly recommended to use the ESAPI library and
			// uncomment the following line to
			// avoid encoded attacks.
			// value = ESAPI.encoder().canonicalize(value);
			// Avoid null characters
			value = value.replaceAll("", "");
			// Avoid anything between script tags
			Pattern scriptPattern = Pattern.compile("(.*?)<script>(.*?)</script>(.*?)", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");

			if (name != null && "act".equals(name)) {
				// act为方法名只允许字母和数字,把其他的字符去掉
				value = value.replaceAll("[^0-9a-zA-Z]", "");
			}

			// Avoid anything in a src='...' type of expression
			scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");
			scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");
			// Remove any lonesome </script> tag
			scriptPattern = Pattern.compile("(.*?)</script>(.*?)", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");
			// Remove any lonesome <script ...> tag
			scriptPattern = Pattern.compile("(.*?)<script(.*?)>(.*?)",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");
			// Avoid eval(...) expressions
			scriptPattern = Pattern.compile("eval\\((.*?)\\)",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid expression(...) expressions
			scriptPattern = Pattern.compile("expression\\((.*?)\\)",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");
			// Avoid javascript:... expressions
			scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");
			// Avoid vbscript:... expressions
			scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");
			// Avoid onload= expressions
			scriptPattern = Pattern.compile("onload(.*?)=",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");
			scriptPattern = Pattern.compile("(.*?)<iframe>(.*?)</iframe>(.*?)", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");
			scriptPattern = Pattern.compile("(.*?)</iframe>(.*?)", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");
			// Remove any lonesome <script ...> tag
			scriptPattern = Pattern.compile("(.*?)<iframe(.*?)>(.*?)",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");
		}
		return value;
	}
	
	public static void main(String[] args) {
		String s = "dddd ccc ...<script>aaaa</script>123 _ddd...";
		s = escape(s);
		System.err.println(s);
	}
}
