/*
 * Copyright 2011 Indiana University.  All rights reserved.
 *
 * Mark H. Wood, IUPUI University Library, 2011-08-11
 */
package edu.iupui.ulib.maven.fileweaver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Represent a portion of an output file:  literal text, or an input file.
 * It is an error to specify both, or neither.
 * @author mwood
 */
public class Part
{
    /**
     * Text of a literal part.
     * @parameter
     */
    private String text;

    public void setText(String text) throws MojoExecutionException
    {
        if (null != path)
            throw new MojoExecutionException("Part cannot have text and path.");
        else
            this.text = text;
    }

    /**
     * Path to an external file part.
     * @parameter
     */
    private File path;

    public void setPath(File path) throws MojoExecutionException
    {
        if (null != text)
            throw new MojoExecutionException("Part cannot have text and path.");
        else
            this.path = path;
    }

    /**
     * Do not append a newline.
     * @parameter default-value="false"
     * @required
     */
    private Boolean nonl = Boolean.FALSE;

    /**
     * @parameter default-value="${{project.build.encoding}"
     * @readonly
     * @required
     */
    private String encoding;

    /**
     * Get a Reader on whatever kind of content we have.
     * @return
     * @throws FileNotFoundException 
     */
    Reader getReader()
            throws FileNotFoundException, MojoExecutionException,
            UnsupportedEncodingException
    {
        Charset charset;
        if (null == encoding)
            charset = Charset.defaultCharset();
        else
            charset = Charset.forName(encoding);

        if (null != text)
            if (nonl)
                return new StringReader(text);
            else
                return new StringReader(text + "\n");
        else if (null != path)
            return new InputStreamReader(new FileInputStream(path),charset);
        else
            throw new MojoExecutionException("Part has neither text nor path.");
    }

    @Override
    public String toString()
    {
        if (null != text)
            return "literal part:  " + text;
        else if (null != path)
            return "file part:  " + path;
        else
            return "unknown part";
    }
}
