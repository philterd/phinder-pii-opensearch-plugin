/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package ai.philterd.phinder.ext;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.core.ParseField;
import org.opensearch.core.common.io.stream.StreamInput;
import org.opensearch.core.common.io.stream.StreamOutput;
import org.opensearch.core.common.io.stream.Writeable;
import org.opensearch.core.xcontent.ObjectParser;
import org.opensearch.core.xcontent.ToXContentObject;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentParser;
import org.opensearch.search.SearchExtBuilder;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * The Phinder parameters available in the ext.
 */
public class PhinderParameters implements Writeable, ToXContentObject {

    private static final ObjectParser<PhinderParameters, Void> PARSER;
    private static final ParseField POLICY_NAME = new ParseField("policy");
    private static final ParseField CONTEXT = new ParseField("context");
    private static final ParseField FIELD_NAME = new ParseField("field");

    static {
        PARSER = new ObjectParser<>(PhinderParametersExtBuilder.PHINDER_PARAMETERS_NAME, PhinderParameters::new);
        PARSER.declareString(PhinderParameters::setPolicy, POLICY_NAME);
        PARSER.declareString(PhinderParameters::setPolicy, CONTEXT);
        PARSER.declareString(PhinderParameters::setPolicy, FIELD_NAME);
    }

    /**
     * Get the {@link PhinderParameters} from a {@link SearchRequest}.
     * @param request A {@link SearchRequest},
     * @return The Phinder {@link PhinderParameters parameters}.
     */
    public static PhinderParameters getPhinderParameters(final SearchRequest request) {

        PhinderParametersExtBuilder builder = null;

        if (request.source() != null && request.source().ext() != null && !request.source().ext().isEmpty()) {
            final Optional<SearchExtBuilder> b = request.source()
                    .ext()
                    .stream()
                    .filter(bldr -> PhinderParametersExtBuilder.PHINDER_PARAMETERS_NAME.equals(bldr.getWriteableName()))
                    .findFirst();
            if (b.isPresent()) {
                builder = (PhinderParametersExtBuilder) b.get();
            }
        }

        if (builder != null) {
            return builder.getParams();
        } else {
            return null;
        }

    }

    private String policy;
    private String context;
    private String fieldName;

    /**
     * Creates a new instance.
     */
    public PhinderParameters() {}

    /**
     * Creates a new instance.
     * @param input The {@link StreamInput} to read parameters from.
     * @throws IOException Thrown if the parameters cannot be read.
     */
    public PhinderParameters(StreamInput input) throws IOException {
        this.policy = input.readString();
        this.context = input.readString();
        this.fieldName = input.readString();
    }

    /**
     * Creates a new instance.
     * @param policy The name of the policy to apply.
     */
    public PhinderParameters(String policy) {
        this.policy = policy;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder xContentBuilder, Params params) throws IOException {
        return xContentBuilder
                .field(POLICY_NAME.getPreferredName(), this.policy);
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeString(policy);
    }

    /**
     * Create the {@link PhinderParameters} from a {@link XContentParser}.
     * @param parser An {@link XContentParser}.
     * @return The {@link PhinderParameters}.
     * @throws IOException Thrown if the parameters cannot be read.
     */
    public static PhinderParameters parse(XContentParser parser) throws IOException {
        return PARSER.parse(parser, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PhinderParameters other = (PhinderParameters) o;
        return Objects.equals(this.policy, other.getPolicy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), this.policy);
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

}