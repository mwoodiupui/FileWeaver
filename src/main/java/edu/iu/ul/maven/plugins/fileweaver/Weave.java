/**
 * Copyright (C) 2011-2012 Indiana University
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * Build files from external and literal portions, with filtering.
 * <p>
 * Any number of output files may be generated.
 *
 * @author Mark H. Wood, IUPUI University Library
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
