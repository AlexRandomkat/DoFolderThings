package stuff;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class for doing stuff with directories/folders
 */
public class Folder extends File {
	private static final long serialVersionUID = 1L;
	private File[] contents;
	private static ArrayList<String> dntExtensions = new ArrayList<String>();
	private static ArrayList<String> incExtensions = new ArrayList<String>();
	private static String tag = null;

	/**
	 * Creates a new Folder that represents a folder/directory
	 * 
	 * @param dir
	 *            The location of the folder
	 */
	Folder(File dir) throws IOException {
		super(dir.getAbsolutePath());
		contents = this.listFiles();
	}

	/**
	 * Creates a new Folder that represents a folder/directory
	 * 
	 * @param dir
	 *            The location of the folder
	 */
	Folder(String dir) {
		super(dir);
		contents = this.listFiles();
	}

	/**
	 * A getter method for obtaining a File[] representing the contents of this
	 * Folder.
	 * 
	 * @return a File[]
	 */
	public File[] getContents() {
		return contents;
	}

	/**
	 * A static method for getting the file type of a file
	 * 
	 * @param f
	 *            A file
	 * @return A string of the file extension (e.g. ".txt" or ".png")
	 */
	public static String getExtension(File f) {
		String path = f.getAbsolutePath();
		if (f.isDirectory() || path.lastIndexOf(".") == -1) {
			return "";
		}
		path = path.substring(path.lastIndexOf("."), path.length());
		return path;

	}

	/**
	 * Renames the contents of a folder to a unique name that is highly unlikely to
	 * overwrite anything else.
	 * 
	 * @return True if the operation was successful, false otherwise.
	 */
	public boolean renameToUnique() {
		String tempTag;
		if (!this.isDirectory()) {
			System.out.println("Cannot rename contents of " + this.getAbsolutePath() + " as it is not a directory.");
			return false;
		}

		if (1 + (int) (Math.log10((contents.length == 0) ? 1 : contents.length)) < 120) {
			// try to keep tempTag not too long. A maximum of 120 characters should suffice.
			// Also, prevent negative infinities please.
			tempTag = Random.Strings
					.randomString(120 - (1 + (int) (Math.log10((contents.length == 0) ? 1 : contents.length))));
		} else {
			System.out.println("Error: Requested change of filenames in " + this.getAbsolutePath()
					+ " is not possible. Try reducing number of files in this folder.");
			return false;
		}

		try {
			for (int i = 0; i < contents.length; i++) {
				/*
				 * It IS possible the tag isn't unique, but it's more likely the computer will
				 * fail before you get a tag that's a duplicate of some file that already
				 * exists. (Or, the sun explodes. Whichever comes first.) Therefore, I'm not
				 * going to waste processing power trying to handle that case.
				 */
				if (contents[i].isFile() && !dntExtensions.contains(getExtension(contents[i]))
						&& (incExtensions.size() == 0) ? true : incExtensions.contains(getExtension(contents[i]))) {
					contents[i].renameTo(new File(
							contents[i].getParent() + File.separator + tempTag + i + getExtension(contents[i])));
				}
			}

			// update contents
			contents = this.listFiles();
			return true;
		} catch (Exception e) {
			System.out.println("While calling (new File(\"" + this.getAbsolutePath()
					+ "\").renameToUnique(), an exception was encountered:\n" + e);
			return false;
		}
	}

	/**
	 * Renames the contents of a folder to a unique name that is highly unlikely to
	 * overwrite anything else, while returning a mapping (previous filename) -->
	 * (current filename) represented by a String[][] map = new String
	 * [contents.length][2]. The mapping is as follows: (map[x][0]) --> (map[x][1])
	 * 
	 * @return A string[][] containing the mapping if the operation was successful,
	 *         null otherwise.
	 */
	public String[][] renameToUniqueWithMapping() {
		String[][] map = new String[contents.length][2];
		String tempTag;
		if (!this.isDirectory()) {
			System.out.println("Cannot rename contents of " + this.getAbsolutePath() + " as it is not a directory.");
			return null;
		}

		if (1 + (int) (Math.log10(((contents.length == 0) ? 1 : contents.length))) < 120) {
			// try to keep tempTag not too long. A maximum of 120 characters should suffice.
			// Also, prevent negative infinities.
			tempTag = Random.Strings
					.randomString(120 - (1 + (int) (Math.log10(((contents.length == 0) ? 1 : contents.length)))));
		} else {
			System.out.println("Error: Requested change of filenames in " + this.getAbsolutePath()
					+ " is not possible. Try reducing number of files in this folder.");
			return null;
		}

		try {
			String name;
			for (int i = 0; i < contents.length; i++) {
				/*
				 * It IS possible the tag isn't unique, but it's more likely the computer will
				 * fail before you get a tag that's a duplicate of some file that already
				 * exists. (Or, the sun explodes. Whichever comes first.) Therefore, I'm not
				 * going to waste processing power trying to handle that case.
				 */
				if (contents[i].isFile() && !dntExtensions.contains(getExtension(contents[i]))
						&& (incExtensions.size() == 0) ? true : incExtensions.contains(getExtension(contents[i]))) {
					map[i][0] = contents[i].getName();
					name = tempTag + i + getExtension(contents[i]);
					contents[i].renameTo(new File(contents[i].getParent() + File.separator + name));

					map[i][1] = name;
				}
			}

			// update contents
			contents = this.listFiles();

			return map;
		} catch (Exception e) {
			System.out.println("While calling (new File(\"" + this.getAbsolutePath()
					+ "\").renameToUnique(), an exception was encountered:\n" + e);
			return null;
		}
	}

	/**
	 * Renames files in this Folder using names generated by
	 * Random.Strings.shuffledAlphanumericStrings(n,length) and adds a tag if one
	 * was initialized.
	 * 
	 * @throws Random.notEnoughCombinations
	 *             Don't worry about it, it probably won't throw this if I did my
	 *             math right...
	 */
	public void shuffleFileNames() throws Random.notEnoughCombinations {
		// check to make sure folder actually exists.
		if (!this.exists()) {
			System.out.println(this.getAbsolutePath() + " does not exist");
			return;
		}

		// find minimum suitable name length
		// Also prevents negative infinity. That's typically good.
		int length = 1 + (int) (Math.log10((contents.length == 0) ? (1 / 36) : contents.length) / Math.log10(36));

		// create file names
		String names[] = Random.Strings.shuffledAlphanumericStrings(contents.length, length);

		// add tag to names, if one exists.
		if (tag != null) {
			for (int i = 0; i < names.length; i++) {
				names[i] = tag + names[i];
			}
		}

		// change filenames to a unique name to avoid conflicts and overwriting
		if (this.renameToUnique()) {
			// change filenames back to intended tag and randomized string
			for (int i = 0; i < contents.length; i++) {
				if (contents[i].isFile() && !dntExtensions.contains(getExtension(contents[i]))
						&& (incExtensions.size() == 0) ? true : incExtensions.contains(getExtension(contents[i]))) {
					contents[i].renameTo(
							new File(contents[i].getParent() + File.separator + names[i] + getExtension(contents[i])));
				}
			}

			// update contents
			contents = this.listFiles();
		} else {
			System.out.println("this.renameToUnique() failed when shuffling this: " + this.getAbsolutePath());
		}
	}

	/**
	 * calls shuffleFileNames() on this Folder and all child directories.
	 * 
	 * @throws Random.notEnoughCombinations
	 *             (don't worry about it, it probably won't throw this if I did my
	 *             math right...)
	 */
	public void shuffleFileNamesChild() throws Random.notEnoughCombinations, IOException {
		if (!this.exists()) {
			System.out.println(this.getAbsolutePath() + " does not exist");
			return;
		}
		// shuffle files
		System.out.println("Shuffling contents of " + this.getAbsolutePath());
		shuffleFileNames();

		// shuffle files in folders iteratively
		for (int i = 0; i < contents.length; i++) {
			if (contents[i].isDirectory()) {
				Folder folder = new Folder(contents[i]);
				folder.shuffleFileNamesChild();
			}
		}
	}

	/**
	 * Sets a tag for all Folder objects.
	 * 
	 * @param tag
	 */
	public static void setFolderClassTag(String tag) {
		Folder.tag = tag;
	}

	/**
	 * Adds a tag to files in a Folder.
	 * 
	 * @param tag
	 *            A string to be added at the front of filenames. If tag==null, then
	 *            this method will default to using the static Folder tag.
	 */
	public void addTagToContents(String tag) {
		if (!this.exists()) {
			System.out.println(this.getAbsolutePath() + " does not exist");
			return;
		}

		// if tag is null, default to static Folder tag.
		if (tag == null) {
			if (Folder.tag == null) {
				// Welp, then there's nothing to be done here.
				return;
			}
			tag = Folder.tag;
		}

		// store current names
		String[] names = new String[contents.length];
		for (int i = 0; i < contents.length; i++) {
			names[i] = contents[i].getName();
		}

		/*
		 * Change filenames to a unique name to avoid conflicts and overwriting and
		 * records a map between new and old names. A map is needed because
		 * someFile.listFiles() is not guaranteed to return files in any particular
		 * order. I was having trouble with names getting swapped around.
		 */
		String[][] map = this.renameToUniqueWithMapping();

		if (map != null) {
			// change files back to original name and add tag
			File tempMap;
			for (int i = 0; i < contents.length; i++) {
				/*
				 * Conditions contents[i].isFile() &&
				 * !dntExtensions.contains(getExtension(contents[i])) && (incExtensions.size()
				 * == 0) ? true : incExtensions.contains(getExtension(contents[i]))should be
				 * already fulfilled by this.renameToUniqueWithMapping()
				 */
				if (map[i][0] != null) {
					// find a file
					tempMap = new File(contents[0].getParent() + File.separator + map[i][1]);

					// change said file to previous name but with tag added.
					tempMap.renameTo(new File(contents[0].getParent() + File.separator + tag + map[i][0]));
				}
			}
		}

		// update contents
		contents = this.listFiles();
	}

	/**
	 * calls addTagToContents(tag) on this Folder and all child directories.
	 */
	public void addTagToContentsChild(String tag) throws IOException {
		if (!this.exists()) {
			System.out.println(this.getAbsolutePath() + " does not exist");
			return;
		}
		// add tag to files
		System.out.println("Adding tag to contents of " + this.getAbsolutePath());
		addTagToContents(tag);

		// add tags to files in folders iteratively
		for (int i = 0; i < contents.length; i++) {
			if (contents[i].isDirectory()) {
				Folder folder = new Folder(contents[i]);
				folder.addTagToContentsChild(tag);
			}
		}
	}

	/**
	 * Ignores certain filetypes when using methods such as addTagToContents(tag)
	 * and shuffleFileNames(). The exclusion arraylist takes precedence over the
	 * inclusion arraylist.
	 * 
	 * @param extension
	 *            The file type to be ignored (e.g.".wav" or ".tmp").
	 */
	public static void extensionIgnoresFolderMethods(String extension) {
		dntExtensions.add(extension);
	}

	/**
	 * Sets only certain filetypes to be modified by Folder method calls. This
	 * method adds filetypes to the list of filetypes that can be changed. The
	 * exclusion arraylist takes precedence over the inclusion arraylist.
	 * 
	 * @param extension
	 *            The file type to be altered by method calls (e.g.".wav" or
	 *            ".tmp").
	 */
	public static void addExtenionToFolderMethods(String extension) {
		incExtensions.add(extension);
	}

	/**
	 * Clears incExtensions, which removes the inclusion filter on Folder methods.
	 */
	public static void turnOffInclusiveExtensions() {
		incExtensions.clear();
	}

	/**
	 * Clears dntExtensions, which removes all previous limitations on filetypes
	 * that were put in extensionIgnoresFolderMethods()
	 */
	public static void turnOffExclusiveExtensions() {
		dntExtensions.clear();
	}
}