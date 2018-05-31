package sonc.utils;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Safe execution of game agent methods. The {@code executeSafelly()} method
 * receives a runnable as argument and executes it within a given timeout and
 * making sure it doesn't use system resources. A typical use is
 * 
 * <pre>
 * SafeExecutor.executeSafelly(() -&gt; agent.move());
 * </pre>
 * 
 * This class follows the <b>template method</b> design pattern to implement
 * safe execution, where the concrete class must implement the {@code Runnable}
 * interface.
 * 
 * @author Jos&eacute; Paulo Leal {@code zp@dcc.fc.up.pt}
 */
public class SafeExecutor {

	private static final long DEFAULT_TIMEOUT = 500L;

	private static long timeout = DEFAULT_TIMEOUT;

	/**
	 * Get current timeout for safe execution
	 * 
	 * @return timeout
	 */
	public static long getTimeout() {
		return timeout;
	}

	/**
	 * Set timeout for safe execution
	 * 
	 * @param timeout
	 *            to enforce
	 */
	public static void setTimeout(long timeout) {
		SafeExecutor.timeout = timeout;
	}

	/**
	 * Class for implementing gated security. When the gates are open (default)
	 * everything is allowed. When they are closed almost everything is
	 * disallowed. Gates are closed just before invoking unsafe methods and
	 * reopened afterwards. This security manager is automatically set when the
	 * host class is loaded.
	 */
	private static class GatedSecurityManager extends SecurityManager {
		private boolean enforce = false;
		private List<String> acceptablePrefixes = new ArrayList<>();

		private GatedSecurityManager() {
			String value;

			for (String name : Arrays.asList("user.dir",
					"eclipse.home.location", "user.home"))
				if ((value = System.getProperty(name)) != null)
					acceptablePrefixes.add(value);

		}

		@Override
		public void checkPermission(Permission perm) {
			if (enforce) {
				if (perm.getActions().endsWith("read")
						&& checkPrefix(perm.getName())) {
					// allow reading local and eclipse files
				} else if (perm.getName().equals("modifyThread")) {
					// the executor used for timeout may need to perform some
					// thread modifications
				} else if (perm.getName().startsWith("exitVM")) {
					throw new SecurityException(
							"access denied (\"java.lang.RuntimePermission\" \"exit\")");
				} else {
					// System.out.println(perm.getActions()+" :
					// "+perm.getName());
					super.checkPermission(perm);
				}
			}
		}

		private boolean checkPrefix(String pathname) {
			for (String prefix : acceptablePrefixes)
				if (pathname.startsWith(prefix))
					return true;
			return false;
		}

		private void tight() {
			enforce = true;
		}

		private void relaxed() {
			enforce = false;
		}
	}

	private static GatedSecurityManager security = new GatedSecurityManager();
	static {
		System.setSecurityManager(security);
	}

	/**
	 * Executes given runnable in a safe environment. Execution is limited to
	 * the previous set timeout and to a strict security manager. It cannot
	 * access the file system, the operating system, the network or any other
	 * sensitive resource. Runnable execution is synchronized since.
	 * 
	 * @param runnable
	 *            the method to execute safely
	 * @throws Exception
	 *             when errors occur in runnable, it tries to use system
	 *             resources or timeout is exceeded
	 */
	static public void executeSafelly(Runnable runnable) throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(1);
		List<Exception> errors = new ArrayList<>();

		synchronized (security) {
			executor.execute(() -> {
				security.tight();
				try {
					runnable.run();
				} catch (Exception e) {
					errors.add(e);
				} finally {
					security.relaxed();
				}
			});
			executor.shutdown();
			try {
				if (!executor.awaitTermination(timeout,
						TimeUnit.MILLISECONDS)) {
					errors.add(new RuntimeException("Timeout"));
				}
			} catch (InterruptedException e) {
				errors.add(e);
			} finally {
				security.relaxed();
			}
		}
		if (errors.size() > 0)
			throw errors.get(0);
	}
}
