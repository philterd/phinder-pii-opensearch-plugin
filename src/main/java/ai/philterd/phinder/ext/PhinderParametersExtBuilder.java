/*
 *     Copyright 2025 Philterd, LLC @ https://www.philterd.ai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.philterd.phinder.ext;

import org.opensearch.core.common.io.stream.StreamInput;
import org.opensearch.core.common.io.stream.StreamOutput;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentParser;
import org.opensearch.search.SearchExtBuilder;

import java.io.IOException;
import java.util.Objects;

/**
 * Subclass of {@link SearchExtBuilder} to access Phinder parameters.
 */
public class PhinderParametersExtBuilder extends SearchExtBuilder {

    /**
     * The name of the "ext" section containing Phinder parameters.
     */
    public static final String PHINDER_PARAMETERS_NAME = "phinder";

    private PhinderParameters params;

    /**
     * Creates a new instance.
     */
    public PhinderParametersExtBuilder() {}

    /**
     * Creates a new instance from a {@link StreamInput}.
     * @param input A {@link StreamInput} containing the parameters.
     * @throws IOException Thrown if the stream cannot be read.
     */
    public PhinderParametersExtBuilder(StreamInput input) throws IOException {
        this.params = new PhinderParameters(input);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), this.params);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof PhinderParametersExtBuilder)) {
            return false;
        }

        return this.params.equals(((PhinderParametersExtBuilder) obj).getParams());
    }

    @Override
    public String getWriteableName() {
        return PHINDER_PARAMETERS_NAME;
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        this.params.writeTo(out);
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        return builder.value(this.params);
    }

    /**
     * Parses the phinder section of the ext block.
     * @param parser A {@link XContentParser parser}.
     * @return The {@link PhinderParameters paramers}.
     * @throws IOException Thrown if the Phinder parameters cannot be read.
     */
    public static PhinderParametersExtBuilder parse(XContentParser parser) throws IOException {
        final PhinderParametersExtBuilder builder = new PhinderParametersExtBuilder();
        builder.setParams(PhinderParameters.parse(parser));
        return builder;
    }

    /**
     * Gets the {@link PhinderParameters params}.
     * @return The {@link PhinderParameters params}.
     */
    public PhinderParameters getParams() {
        return params;
    }

    /**
     * Set the {@link PhinderParameters params}.
     * @param params The {@link PhinderParameters params}.
     */
    public void setParams(PhinderParameters params) {
        this.params = params;
    }

}