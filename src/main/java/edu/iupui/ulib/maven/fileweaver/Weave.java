/*
 * Copyright 2011 Indiana University.  All rights reserved.
 * 
 * Mark H. Wood, IUPUI University Library, 2011-08-11
 */
package edu.iupui.ulib.maven.fileweaver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * Build files from external and literal portions, with filtering.
 * <p>
 * Any number of output files may be generated.
 *
 * @goal weave
 * 
 * @phase generate-sources
 */
public class Weave
    extends AbstractMojo
{
    /**
     * Where to write files, unless a &lt;part&gt; specifies otherwise.
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private java.io.File outputPath;

    /**
     * Files to be woven.
     * @parameter
     * @required
     */
    private List<Output> outputs;

    /**
     * Common properties for filtering all outputs.
     * @parameter
     */
    private Map<String, String> properties;

    @Override
    public void execute()
        throws MojoExecutionException
    {
        final Log log = getLog();

        Map<Object, Object> executionProps = new HashMap<Object, Object>();
        if (null != properties)
            executionProps.putAll(properties);

        int nFiles = 0;
        for (Output file : outputs)
        {
            file.build(outputPath, executionProps, log);
            nFiles++;
        }
        log.info(String.format("%d files woven.", nFiles));
    }
}
