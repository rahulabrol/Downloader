package com.downloader;

/**
 * Created by Rahul Abrol on 12/5/17.
 * <p>
 * Enum used to define the type of image file used in
 * the directory creation.
 */

public enum FileType {
    JPG("jpg"), JPEG("jpeg"), PNG("png"),
    DOC("doc"), DOCX("docx"), PDF("pdf"),
    SQL("sql"), HTML("html"), TXT("txt"),
    MP4("mp4"), a3GP("3gp");

    private String value;

    /**
     * Constructor used to assign the value.
     *
     * @param value valued defined in the enum.
     */
    private FileType(final String value) {
        this.value = value;
    }

    /**
     * Method used to get the property of enum.
     *
     * @param string that we want to match.
     * @return FileType
     * @throws Exception execption if any.
     */
    static FileType fromPropertyName(final String string) throws Exception {
        for (FileType currentType : FileType.values()) {
            if (string.equalsIgnoreCase(currentType.getValue())) {
                return currentType;
            }
        }
        throw new Exception("Unmatched Type: " + string);
    }

    public String getValue() {
        return value;
    }
}
