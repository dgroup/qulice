/**
 * Copyright (c) 2011, Qulice.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the Qulice.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.qulice.checkstyle;

import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

/**
 * Test case for {@link CheckstyleValidator} class.
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public final class CheckstyleValidatorTest {

    /**
     * Name of property to set to change location of the license.
     */
    private static final String LICENSE_PROP = "license";

    /**
     * Temporary folder, set by JUnit framework automatically.
     * @checkstyle VisibilityModifier (3 lines)
     */
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    /**
     * The folder where to test.
     * @see #prepare()
     */
    private File folder;

    /**
     * The environment.
     * @see #prepare()
     */
    private Environment env;

    /**
     * Prepare the folder and environment for testing.
     * @throws Exception If something wrong happens inside
     */
    @Before
    public void prepare() throws Exception {
        this.folder = this.temp.newFolder("temp-src");
        this.env = Mockito.mock(Environment.class);
        Mockito.doReturn(this.folder).when(this.env).basedir();
        Mockito.doReturn(this.folder).when(this.env).tempdir();
    }

    /**
     * Validate set of files with error inside.
     * @throws Exception If something wrong happens inside
     */
    @Test(expected = ValidationException.class)
    public void testValidatesSetOfFiles() throws Exception {
        final File license = this.temp.newFile("license.txt");
        FileUtils.writeStringToFile(license, "license\n");
        Mockito.doReturn(this.toURL(license)).when(this.env)
            .param(Mockito.eq(this.LICENSE_PROP), Mockito.any(String.class));
        final Validator validator = new CheckstyleValidator();
        final File java = new File(this.folder, "src/main/java/Main.java");
        java.getParentFile().mkdirs();
        FileUtils.writeStringToFile(java, "public class Main { }");
        validator.validate(this.env);
    }

    /**
     * Immidate the license inside the classpath (validator has to find it).
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void testImmitatesLicenseInClasspath() throws Exception {
        final File license = new File(this.folder, "my-license.txt");
        FileUtils.writeStringToFile(license, "some non-important text\n");
        Mockito.doReturn(this.toURL(license)).when(this.env)
            .param(Mockito.eq(this.LICENSE_PROP), Mockito.any(String.class));
        final Validator validator = new CheckstyleValidator();
        validator.validate(this.env);
    }

    /**
     * Convert file name to URL.
     * @param file The file
     * @return The URL
     */
    private String toURL(final File file) {
        return String.format("file:%s", file);
    }

}