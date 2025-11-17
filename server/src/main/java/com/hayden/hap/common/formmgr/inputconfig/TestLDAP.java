package com.hayden.hap.common.formmgr.inputconfig;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

/**
 * 
 * @author zhangfeng
 * @date 2017年8月2日
 */
public class TestLDAP {

	private static final String BASE_DN = "dc=maxcrc,dc=com";
	
	/** 
	 * 连接LDAP 
	 */  
	@SuppressWarnings({ "rawtypes", "unchecked" })  
	public LdapContext connetLDAP() throws NamingException {  
		// 连接Ldap需要的信息  
		String ldapFactory = "com.sun.jndi.ldap.LdapCtxFactory";  
		String ldapUrl = "ldap://ZHANGFENG:389";// url  
		//		String ldapAccount = "cn=root"; // 用户名  
		String root = "cn=Manager,dc=maxcrc,dc=com"; 
		String ldapPwd = "secret";//密码  
		Hashtable env = new Hashtable();  
		env.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);  
		// LDAP server  
		env.put(Context.PROVIDER_URL, ldapUrl);  
		env.put(Context.SECURITY_AUTHENTICATION, "simple");  
		env.put(Context.SECURITY_PRINCIPAL, root);  
		env.put(Context.SECURITY_CREDENTIALS, ldapPwd);  
		env.put("java.naming.referral", "follow");  
		LdapContext ctxTDS = new InitialLdapContext(env, null);  
		return ctxTDS;  
	}

	//查询  
	public void testSearch() throws Exception {  
		LdapContext ctx = connetLDAP();   
		String filter = "(&(objectClass=top)(objectClass=organizationalPerson))";  
		// 限制要查询的字段内容  
		String[] attrPersonArray = { "uid", "userPassword", "displayName", "cn", "sn", "mail", "description" };  
		SearchControls searchControls = new SearchControls();  
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);  
		// 设置将被返回的Attribute  
		searchControls.setReturningAttributes(attrPersonArray);  
		// 三个参数分别为：  
		// 上下文；  
		// 要搜索的属性，如果为空或 null，则返回目标上下文中的所有对象；  
		// 控制搜索的搜索控件，如果为 null，则使用默认的搜索控件  
		NamingEnumeration<SearchResult> answer = ctx.search(BASE_DN, filter, searchControls);  
		//         NamingEnumeration<SearchResult> answer = ctx.search("dc=maxcrc,dc=com", null);
		// 输出查到的数据  
		while (answer.hasMore()) {  
			SearchResult result = answer.next();  
			NamingEnumeration<? extends Attribute> attrs = result.getAttributes().getAll();  
			while (attrs.hasMore()) {  
				Attribute attr = attrs.next();  
				if("userPassword".equals(attr.getID())) {
					byte[] bytes=(byte[])attr.get(0);
					String passwordValue=new String(bytes);
					System.out.println(attr.getID() + "=" + passwordValue);
				}else {
					System.out.println(attr.getID() + "=" + attr.get());
				}				
			}          	                      
		}  
	}  

	// 添加用户  
	public boolean addUserLdap(String account, String password) {   
		LdapContext ctx = null;
		try {
			ctx = connetLDAP();  
			BasicAttributes attrsbu = new BasicAttributes();  
			BasicAttribute objclassSet = new BasicAttribute("objectclass");  
			objclassSet.add("person");  
			objclassSet.add("top");  
			objclassSet.add("organizationalPerson");  
			objclassSet.add("inetOrgPerson");  
			attrsbu.put(objclassSet);  
			attrsbu.put("sn", account);  
			attrsbu.put("uid", account);  
			attrsbu.put("userPassword", password);
			ctx.createSubcontext("cn=" + account + ",ou=People,dc=maxcrc,dc=com", attrsbu);  
			ctx.close();  
			return true;  
		} catch (NamingException ex) {  
			try {  
				if (ctx != null) {  
					ctx.close();  
				}  
			} catch (NamingException namingException) {  
				namingException.printStackTrace();  
			}  
			ex.printStackTrace();
		}  
		return false;  
	} 


	public boolean authenricate(String UID, String password) throws NamingException {
		boolean valide = false;

		LdapContext ctx = connetLDAP();
		System.out.println(getUserDN(UID, ctx));
		try {
			// LDAP server   
			ctx.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");  
			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, "cn=b1,ou=People,dc=maxcrc,dc=com");
			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, "1");
			ctx.reconnect(null);

			valide = true;
		} catch (AuthenticationException e) {
			e.printStackTrace();
			valide = false;
		} catch (NamingException e) {
			e.printStackTrace();
			valide = false;
		}
		ctx.close();
		return valide;
	}

	private String getUserDN(String cn, LdapContext ctx) {
		String userDN = "";
		try {
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

			NamingEnumeration<SearchResult> en = ctx.search("dc=maxcrc,dc=com", "cn=" + cn, constraints);

			if (en == null || !en.hasMoreElements()) {
				System.out.println("未找到该用户");
			}
			// maybe more than one element
			while (en != null && en.hasMoreElements()) {
				Object obj = en.nextElement();
				if (obj instanceof SearchResult) {
					SearchResult si = (SearchResult) obj;
					userDN += si.getName();
					userDN += ",dc=maxcrc,dc=com";
				} else {
					System.out.println(obj);
				}
			}
		} catch (Exception e) {
			System.out.println("查找用户时产生异常。");
			e.printStackTrace();
		}

		return userDN;
	}


	public static void main(String[] args) throws Exception {
		TestLDAP t1 = new TestLDAP();
				t1.testSearch();
		//
		//		//		t1.addUser("cn=a1,dc=maxcrc,dc=com", "1", "abc", "aaa");
		//		t1.addUserLdap("c1", "123456");
		//		t1.addUserLdap("icc", "这是一个非常复杂的密码");
		//		t1.testSearch();

//		boolean result = t1.authenricate("icc", "这是一个非常复杂的密码");
//		System.out.println(result);
	}
}
