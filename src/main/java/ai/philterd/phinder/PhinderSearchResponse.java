/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package ai.philterd.phinder;

import ai.philterd.phinder.ext.PhinderParametersExtBuilder;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.search.SearchResponseSections;
import org.opensearch.action.search.ShardSearchFailure;
import org.opensearch.core.xcontent.ToXContent;
import org.opensearch.core.xcontent.XContentBuilder;

import java.io.IOException;

public class PhinderSearchResponse extends SearchResponse {

    private static final String EXT_SECTION_NAME = "ext";
    private static final String PHINDER_POLICY_NAME = "policy";

    private final String policy;

    /**
     * Creates a new Phinder search response.
     *
     * @param internalResponse The internal response.
     * @param scrollId         The scroll ID.
     * @param totalShards      The count total shards.
     * @param successfulShards The count of successful shards.
     * @param skippedShards    The count of skipped shards.
     * @param tookInMillis     The time took in milliseconds.
     * @param shardFailures    An array of {@link ShardSearchFailure}.
     * @param clusters         The {@link Clusters}.
     * @param policy       The name of the policy.
     */
    public PhinderSearchResponse(
            SearchResponseSections internalResponse,
            String scrollId,
            int totalShards,
            int successfulShards,
            int skippedShards,
            long tookInMillis,
            ShardSearchFailure[] shardFailures,
            SearchResponse.Clusters clusters,
            String policy
    ) {
        super(internalResponse, scrollId, totalShards, successfulShards, skippedShards, tookInMillis, shardFailures, clusters);
        this.policy = policy;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, ToXContent.Params params) throws IOException {

        builder.startObject();
        innerToXContent(builder, params);

        builder.startObject(EXT_SECTION_NAME);
        builder.startObject(PhinderParametersExtBuilder.PHINDER_PARAMETERS_NAME);
        builder.field(PHINDER_POLICY_NAME, this.policy);
        builder.endObject();
        builder.endObject();
        builder.endObject();

        return builder;

    }

}
