package org.hmdms.hmmanager.core;

/**
 * Represents a document in the dms
 */
public class Document extends Element {
    /**
     * File extension without leading dot
     */
    private String extension;
    /**
     * Filesize in kb
     */
    private double fileSize;

    public Document() { }

    /**
     * Returns file extension as String without leading dot
     * @return File extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Set the file extension of the document. Extension should be passed without leading dot
     * @param extension File extension
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * Returns the filesize in kb
     * @return Filesize in kb
     */
    public double getFileSize() {
        return fileSize;
    }

    /**
     * Sets the filesize of the document
     * @param fileSize Filesize of the document in kb
     */
    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

}
