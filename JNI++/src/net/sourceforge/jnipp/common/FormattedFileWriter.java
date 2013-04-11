package net.sourceforge.jnipp.common;

import java.io.*;
import java.util.*;

public class FormattedFileWriter {

    private String fileName = null;
    private int tabLevel = 0;
    private boolean needsTabs = true;
    private ArrayList text = new ArrayList();
    private ArrayList mergeText = null;
    private StringWriter writer = new StringWriter();

    public FormattedFileWriter(String fileName)
            throws IOException {
        this(fileName, false);
    }

    public FormattedFileWriter(String fileName, boolean destroy)
            throws IOException {
        this.fileName = fileName;
        init(destroy);
    }

    private void init(boolean destroy)
            throws IOException, FileNotFoundException {
        int pos = fileName.lastIndexOf(File.separatorChar);
        if (pos != -1) {
            String path = fileName.substring(0, fileName.lastIndexOf(File.separatorChar));
            File pathFile = new File(path);
            pathFile.mkdirs();
        }

        if (destroy == true) {
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            mergeText = new ArrayList();
            while (reader.ready() == true) {
                mergeText.add(reader.readLine());
            }
        } catch (FileNotFoundException ex) {
            // no existing file to merge with -- ignore exception
        }
    }

    public void flush() throws IOException {
        FileWriter fw = new FileWriter(fileName);
        Iterator iter = mergeText().iterator();

        while (iter.hasNext() == true) {
            fw.write((String) iter.next() + "\n");
        }

        fw.flush();
        fw.close();
    }

    private ArrayList mergeText() {
        if (mergeText == null) {
            return text;
        }

        Object[] textArr = text.toArray();
        Object[] mergeTextArr = mergeText.toArray();
        String s1 = null;
        String s2 = null;

        ArrayList mergeResult = new ArrayList();
        ArrayList workingText = new ArrayList();

        int j = 0;
        for (int i = 0; i < textArr.length; ++i, ++j) {
            s1 = (String) textArr[i];
            int k = j;
            while (k < mergeTextArr.length) {
                s2 = (String) mergeTextArr[k++];
                if (s1.equals(s2) == false) {
                    workingText.add(s2);
                } else {
                    break;
                }
            }

            if (k < mergeTextArr.length && workingText.isEmpty() == false) {
                mergeResult.addAll(workingText);
                j = k - 1;
            }
            mergeResult.add(s1);
            workingText.clear();
        }

        if (j < mergeTextArr.length && s1.equals(mergeTextArr[j]) == true) {
            ++j;
        }

        for (; j < mergeTextArr.length; ++j) {
            if (mergeTextArr[j] != null) {
                mergeResult.add((String) mergeTextArr[j]);
            }
        }

        return mergeResult;
    }

    public void close()
            throws IOException {
    }

    public void incTabLevel() {
        ++tabLevel;
    }

    public void decTabLevel() {
        --tabLevel;
    }

    public void output(String line)
            throws IOException {
        if (needsTabs == true) {
            for (int i = tabLevel; i > 0; --i) {
                writer.write("\t");
            }
            needsTabs = false;
        }

        writer.write(line);
    }

    public void outputLine(String line)
            throws IOException {
        output(line);
        text.add(writer.toString());
        writer = new StringWriter();
        needsTabs = true;
    }

    public void newLine(int numLines)
            throws IOException {
        if (writer.toString().equals("") == false) {
            text.add(writer.toString());
        }
        writer = new StringWriter();
        for (int i = numLines; i > 0; --i) {
            text.add("");
        }
        needsTabs = true;
    }
}
