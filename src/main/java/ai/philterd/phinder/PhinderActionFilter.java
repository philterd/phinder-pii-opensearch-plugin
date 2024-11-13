/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package ai.philterd.phinder;

import ai.philterd.phileas.model.enums.MimeType;
import ai.philterd.phileas.model.responses.FilterResponse;
import ai.philterd.phileas.services.PhileasFilterService;
import ai.philterd.phinder.ext.PhinderParameters;
import org.opensearch.action.ActionRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.support.ActionFilter;
import org.opensearch.action.support.ActionFilterChain;
import org.opensearch.core.action.ActionListener;
import org.opensearch.core.action.ActionResponse;
import org.opensearch.search.SearchHit;
import org.opensearch.tasks.Task;

import java.util.List;

public class PhinderActionFilter implements ActionFilter {

    private final PhileasFilterService phileasFilterService;

    public PhinderActionFilter(final PhileasFilterService phileasFilterService) {
        this.phileasFilterService = phileasFilterService;
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse> void apply(
            Task task,
            String action,
            Request request,
            ActionListener<Response> listener,
            ActionFilterChain<Request, Response> chain
    ) {

        if (!(request instanceof SearchRequest)) {
            chain.proceed(task, action, request, listener);
            return;
        }

        chain.proceed(task, action, request, new ActionListener<>() {

            @Override
            public void onResponse(Response response) {

                if(request instanceof SearchRequest) {

                    try {

                        response = (Response) handleSearchRequest((SearchRequest) request, response);

                    } catch (Exception ex) {
                        throw new RuntimeException("Unable to apply Philes to search hit.", ex);
                    }

                }

                listener.onResponse(response);

            }

            @Override
            public void onFailure(Exception ex) {
                listener.onFailure(ex);
            }

        });

    }

    private ActionResponse handleSearchRequest(final SearchRequest searchRequest, ActionResponse response) throws Exception {

        if (response instanceof SearchResponse) {

            final PhinderParameters phinderParameters = PhinderParameters.getPhinderParameters(searchRequest);

            if (phinderParameters != null) {

                final String policyName = phinderParameters.getPolicyName();
                final String context = phinderParameters.getContext();
                final String fieldName = phinderParameters.getFieldName();

                for (final SearchHit hit : ((SearchResponse) response).getHits()) {

                    // Look for PII by applying the policy.
                    final String input = hit.field(fieldName).getValue().toString();
                    final FilterResponse filterResponse = phileasFilterService.filter(List.of(policyName), context, hit.getId(), input, MimeType.TEXT_PLAIN);

                }

                final SearchResponse searchResponse = (SearchResponse) response;

                response = new PhinderSearchResponse(
                        searchResponse.getInternalResponse(),
                        searchResponse.getScrollId(),
                        searchResponse.getTotalShards(),
                        searchResponse.getSuccessfulShards(),
                        searchResponse.getSkippedShards(),
                        searchResponse.getTook().millis(),
                        searchResponse.getShardFailures(),
                        searchResponse.getClusters(),
                        policyName  // TODO: Include the results of the PII scan.
                );

            }

        }

        return response;

    }

}
