/**
 * Copyright (C) 2011 Indiana University
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
package edu.iupui.ulib.maven.fileweaver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.InterpolationFilterReader;

/**
 * Attributes of a file to be assembled.
 * 
 * @author mwood
 */
public class Output
{
    private static final int BUFFER_SIZE = 256;

    /**
     * Name of the output file.
     * @parameter
     * @required
     */
    private String name;

    /**
     * Path to the file's directory.  Defaults to the plugin's output directory.
     * @parameter
     */
    private File outputPath;

    /**
     * Properties for interpolation in this file only.
     * @parameter
     */
    private Map<String, String> properties;

    /**
     * Sources from which to assemble the output.
     * @parameter
     * @required
     */
    private List<Part> parts;

    /**
     * @parameter default-value="${project.build.sourceEncoding}"
     * @required
     * @readonly
     */
    private String encoding;

    /** output path after defaults are applied */
    private File finalPath = null;

    /**
     * Create the output file and return a Writer on it.
     * @param defaultPath directory in which to create if this WovenFile does
     * not specify.
     * @return a Writer on the open file.
     * @throws FileNotFoundException, IOException
     */
    Writer open(File defaultPath) throws FileNotFoundException, IOException
    {
        File pathTo;
        if (null != outputPath)
            pathTo = outputPath;
        else
            pathTo = defaultPath;
        finalPath = new File(pathTo, name);

        // Ensure that the output path exists
        File parentPath = finalPath.getParentFile();
        if (null != parentPath)
            parentPath.mkdirs();
        
        Charset charset;
        if (null == encoding)
            charset = Charset.defaultCharset();
        else
            charset = Charset.forName(encoding);

        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                finalPath), charset));
    }

    /**
     * Assemble the file's parts and write out.
     * @param defaultPath place to put the output if not specified here.
     * @param executionProps properties to be merged with this file's own (may be null).
     * @param log a logger, if needed.
     * @throws MojoExecutionException on I/O errors.
     */
    void build(File defaultPath, Map<Object, Object> executionProps, Log log)
            throws MojoExecutionException
    {
        Map<Object, Object> fileProps = new HashMap<Object, Object>();
        if (null != executionProps)
            fileProps.putAll(executionProps);
        if (null != properties)
            fileProps.putAll(properties);

        CharBuffer bupher = CharBuffer.allocate(BUFFER_SIZE);
        Writer output = null;
        try
        {
            output = open(defaultPath);

            int nParts = 0;
            for (Part part : parts)
            {
                Reader ir = new InterpolationFilterReader(part.getReader(),
                        fileProps);
                bupher.clear();
                while (ir.read(bupher) >= 0)
                {
                    bupher.flip();
                    output.write(bupher.toString());
                    bupher.clear();
                }
                nParts++;
            }
            output.close();
            log.info(String.format("Wove %s from %d parts.",
                    finalPath, nParts));
        } catch (Exception e)
        {
            throw new MojoExecutionException("Could not write " + finalPath, e);
        }
    }

    @Override
    public String toString()
    {
        StringBuilder value = new StringBuilder(32);

        value.append("File:\n");
        value.append("  name:  ").append(name).append('\n');
        if (null != outputPath)
            value.append("  outputPath").append(outputPath);
        for (Part part : parts)
            value.append(part.toString()).append('\n');

        if (null != properties)
            for (String key : properties.keySet())
            {
                value.append("  property:  ").append(key).append(" = ")
                        .append(properties.get(key));
            }

        return value.toString();
    }
}
