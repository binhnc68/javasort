package com.javacore.comparatorN;

public class sortFileExport {
	
	File rootPathFile = new File(rootPath);
    File[] listFile = rootPathFile.listFiles();
    this.sortFileExport(listFile);

	public void sortFileExport(File[] listFile) throws Exception {
        try {
            Arrays.sort(listFile, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    try {
                        String fileName1 = f1.getName();
                        String fileName2 = f2.getName();
                        int temp1 = Integer.parseInt(fileName1.replace(".xml", ""));
                        int temp2 = Integer.parseInt(fileName2.replace(".xml", ""));
                        return Integer.valueOf(temp1).compareTo(temp2);
                    } catch (Exception e) {
                        return 0;
                    }
                }
            });
        } catch (Exception ex) {
            return;
        }
    }
	
	
}
