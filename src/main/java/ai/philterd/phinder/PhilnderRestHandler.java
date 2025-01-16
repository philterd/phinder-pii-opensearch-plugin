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
package ai.philterd.phinder;

import org.opensearch.action.delete.DeleteRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.support.WriteRequest;
import org.opensearch.client.Client;
import org.opensearch.client.node.NodeClient;
import org.opensearch.core.rest.RestStatus;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PhilnderRestHandler extends BaseRestHandler {

    private static final String POLICIES_URL = "_plugins/phileas/policies";

    private static final String POLICIES_INDEX = "phileas_policies";

    private final Client client;

    public PhilnderRestHandler(final Client client) {
        this.client = client;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public List<Route> routes() {
        return List.of(
                new Route(RestRequest.Method.POST, POLICIES_URL),
                new Route(RestRequest.Method.DELETE, POLICIES_URL));
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest restRequest, NodeClient nodeClient) throws IOException {

        if(restRequest.path().equals(POLICIES_URL)) {

            if(restRequest.method() == RestRequest.Method.POST) {

                final String policyName = restRequest.param("name");
                final String policy = restRequest.content().utf8ToString();

                final String id = UUID.randomUUID().toString();

                final Map<String, Object> document = new HashMap<>();
                document.put("name", policyName);
                document.put("policy", policy);

                final IndexRequest indexRequest = new IndexRequest().index(POLICIES_INDEX)
                        .id(id)
                        .source(document)
                        .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);

                client.index(indexRequest);

                return restChannel -> restChannel.sendResponse(new BytesRestResponse(RestStatus.CREATED, "{\"id\":\"" + id + "\""));

            } else if(restRequest.method() == RestRequest.Method.DELETE) {

                final String policyId = restRequest.param("id");

                final DeleteRequest deleteRequest = new DeleteRequest(POLICIES_INDEX, policyId);
                client.delete(deleteRequest);

                return restChannel -> restChannel.sendResponse(new BytesRestResponse(RestStatus.OK, "Policy deleted"));

            } else {
                return restChannel -> restChannel.sendResponse(new BytesRestResponse(RestStatus.METHOD_NOT_ALLOWED, restRequest.method() + " is not allowed."));
            }

        } else {
            return restChannel -> restChannel.sendResponse(new BytesRestResponse(RestStatus.NOT_FOUND, restRequest.path() + " is not found."));
        }

    }

}
