/**
 * Copyright (C) 2011-2018 Indiana University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.ul.maven.plugins.fileweaver;

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
