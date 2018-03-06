package stuff;

import java.io.IOException;

public class Driver {
	public static void main(String[] args) throws Random.notEnoughCombinations, IOException {

		// ignores these types of files when shuffling and adding tags
		Folder.extensionIgnoresFolderMethods(".txt");
		Folder.extensionIgnoresFolderMethods(".7z");

		// set tag for files
		Folder.setFolderClassTag("zzz");

		// declare root directories
		Folder[] shtuff = { new Folder("H:\\Floof"), new Folder("H:\\notFloof"), new Folder("H:\\Other") };

		// shuffle everything and add in tag
		for (int i = 0; i < shtuff.length; i++) {
			shtuff[i].shuffleFileNamesChild();
		}

		System.out.println("\n\n\nFinished");

	}
}
