package sonc.utils;

import java.io.BufferedWriter;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NameNotFoundException;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * Players send Java code that must be compiled and instanced. 
 * The submitted code is expected to be in the default package (no package declaration)
 * and frequently will have class names that may clash with those of other players.
 * To prevent name clashes the code will be wrapped in a package, using the
 * player's id as package name. Then, it will compiled with system's compiler,
 * loaded and instanced. The 
 * <code>AgentBuilder.getInstance(Class&lt;?&gt; type,String code,String packageName)</code>
 * method is used created an instance of the class, compiled from the given code,
 * wrapped on the given package name.
 * 
 * <pre>
 * AgentBuilder builder = new AgentBuilder();
 * Ship playerShip = builder.getInstance(Ship.class,code,player);
 * </pre>
 * 
 * @author Jos&eacute; Paulo Leal {@code zp@dcc.fc.up.pt}
 */
public class AgentBuilder {
	private Path tmp;
	
	public AgentBuilder() throws IOException {	
		tmp = Files.createTempDirectory("builder");
	}
	
	/**
	 * Create an instance of type <code>clazz</code> from <code>code</code>.
	 * The java code is in the default package (no package declaration).
	 * This code is first wrapped in a package with given name, compiled,
	 * loaded and finally instanced. It is assumed that the class has an
	 * empty constructor, most probably the default constructor
	 *  
	 * @param <T> type of class for instantiation
	 *  
	 * @param clazz of the program
	 * @param code of the class
	 * @param packageName that wraps the class
	 * 
	 * @return instance of requested class
	 * @throws IOException on file system errors
	 * @throws InstantiationException on errors instantiating the class 
	 * @throws IllegalAccessException visibility problems with given code
	 * @throws NameNotFoundException if class code has no declared name
	 */
	public <T> T getInstance(Class<T> clazz, String code,String packageName) 
			throws IOException, InstantiationException, IllegalAccessException, NameNotFoundException {
		final String name =  getClassName(code);
		final Path wrappedCodede = wrapCode(packageName, name, code);
		final Path classFile = compile(wrappedCodede);
		final ClassLoaderFromTemp loader = new ClassLoaderFromTemp();
		final Class<?> p1Class = loader.findClassInTemp(classFile);
		final Object instace = p1Class.newInstance();
		
		cleanupTmp();
		
		return clazz.cast(instace);
	}
	
	private static final Pattern CLASS_NAME_RE= Pattern.compile("(?:^|\\s)class\\s+([^\\s]+)");
	
	/**
	 * Extract class name from Java code
	 * @param code with Java class 
	 * @return name of class
	 * @throws NameNotFoundException if no class declaration is found
	 */
	String getClassName(String code) throws NameNotFoundException {
		final Matcher matcher = CLASS_NAME_RE.matcher(code);
		
		if(matcher.find())
			return matcher.group(1);
		else
			throw new NameNotFoundException();
	}
	
	/**
	 * Specialized loader for classes in arbitrary paths, such as temporary directory
	 */
	private static class ClassLoaderFromTemp extends ClassLoader {
		Class<?> findClassInTemp(Path path) throws IOException {
			final byte[] bytes = Files.readAllBytes(path);

			return defineClass(null, bytes, 0, bytes.length);
		}
	}

	/**
	 * Creates a Java file for given class code in given package name.
	 * The package declaration is added to the code and the file
	 * is placed within a directory with that name. The code is expected
	 * to be in the default package (no package declaration).
	 * 
	 * @param packageName to add to class
	 * @param name of class
	 * @param code of class
	 * @return path to created file
	 * @throws IOException on file system errors
	 */
	private Path wrapCode (String packageName,String name,String code) throws IOException {
		final Path packageDir = tmp.resolve(packageName);
		final Path codeFile = packageDir.resolve(name + ".java");
		
		Files.createDirectory(packageDir);
		try(BufferedWriter writer = Files.newBufferedWriter(codeFile)) {
			writer.append("package " + packageName + ";\n");
			writer.append(code);
		}
		
		return codeFile;
	}
	
	private static final Pattern JAVA_RE = Pattern.compile("\\.java$");
	
	/**
	 * Returns a class file for given source Java file.
	 * The class file is produced by compiling the source file. 
	 *  
	 * @param prog path to java class
	 * @return path to compiled class
	 * @throws IOException on I/O errors related to given path
	 */
	private Path compile(Path prog) throws IOException {
		final String source = tmp.relativize(prog).toString();
		final Matcher matcher = JAVA_RE.matcher(source);
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		final OutputStream outStream = System.out;
		final OutputStream errStream = new MyFilterOutputStream(System.err);
		final int result = compiler.run(null, outStream, errStream,getCompilationArguments(prog));
		
		if(result != 0)
			throw error("Compilation failed");
		else if(matcher.find())
			return tmp.resolve(matcher.replaceFirst(".class"));
		else
			throw error("File without java extension");
	};
	
	static List<String> classPaths = new ArrayList<>();
	static {
		classPaths.add("bin");
	}
	
	/**
	 * Add a directory to the class path used in compilation 
	 * 
	 * @param cp class path directory
	 */
	public static void addToClassPath(String cp) {
		classPaths.add(cp);
	}
	
	/**
	 * Produce an array of strings with the arguments needed for compilation
	 * including class path (-cp), destination directory (-d) and class to be compiled.
	 * Class path directories are collected from 
	 * 
	 * @param prog
	 * @return
	 */
	private String[] getCompilationArguments(Path prog) {
		final String[] args = new String[2*(classPaths.size()+1)+1];
		int i=0;
		
		for(String cp: classPaths) {
			args[i++] = "-cp";
			args[i++] = cp;
		}
		args[i++] = "-d";	
		args[i++] = tmp.toString();
		
		args[i++] = prog.toString();
		
		return args;
	}
	
	
	private final static Pattern IGNORE_RE = Pattern.compile(
			"(warning: Supported source version)|(\\d warning)");
	
	/**
	 * Filter stream to remove lines matching previous regular expression
	 * This is a workaround to ignore annoying messages about supported versions.
	 */
	private static class MyFilterOutputStream extends FilterOutputStream {

		public MyFilterOutputStream(OutputStream out) {
			super(out);
		}
		
		public void write(byte[] b, int off, int len) throws IOException {
			
			if(! IGNORE_RE.matcher(new String(b,off,len)).find()) 
				out.write(b,off,len);
		}
	}
	
	/**
	 * Create an exception and make sure the temporary directory is cleanup.
	 *  
	 * @param message for RuntimeException
	 * @return exception 
	 * @throws IOException on file system errors during cleanup
	 */
	private RuntimeException error(String message) throws IOException {
		cleanupTmp();
		return new RuntimeException(message);
	}
	
	/**
	 * Remove all the content in the temporary directory.
	 * 
	 * @throws IOException on I/O errors related to the removed files
	 */
	private void cleanupTmp() throws IOException {
		Files.walkFileTree(tmp, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
		
		Files.deleteIfExists(tmp);
	}
}
