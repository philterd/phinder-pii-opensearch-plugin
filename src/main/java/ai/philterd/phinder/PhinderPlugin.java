/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package ai.philterd.phinder;

import ai.philterd.phileas.model.configuration.PhileasConfiguration;
import ai.philterd.phileas.services.PhileasFilterService;
import ai.philterd.phinder.ext.PhinderParametersExtBuilder;
import org.opensearch.action.support.ActionFilter;
import org.opensearch.client.Client;
import org.opensearch.cluster.metadata.IndexNameExpressionResolver;
import org.opensearch.cluster.node.DiscoveryNodes;
import org.opensearch.cluster.service.ClusterService;
import org.opensearch.common.settings.ClusterSettings;
import org.opensearch.common.settings.IndexScopedSettings;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.settings.SettingsFilter;
import org.opensearch.core.common.io.stream.NamedWriteableRegistry;
import org.opensearch.core.xcontent.NamedXContentRegistry;
import org.opensearch.env.Environment;
import org.opensearch.env.NodeEnvironment;
import org.opensearch.plugins.ActionPlugin;
import org.opensearch.plugins.Plugin;
import org.opensearch.plugins.SearchPlugin;
import org.opensearch.repositories.RepositoriesService;
import org.opensearch.rest.RestController;
import org.opensearch.rest.RestHandler;
import org.opensearch.script.ScriptService;
import org.opensearch.threadpool.ThreadPool;
import org.opensearch.watcher.ResourceWatcherService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;

public class PhinderPlugin extends Plugin implements ActionPlugin, SearchPlugin {

    private Client client;

    @Override
    public Collection<Object> createComponents(
            final Client client,
            final ClusterService clusterService,
            final ThreadPool threadPool,
            final ResourceWatcherService resourceWatcherService,
            final ScriptService scriptService,
            final NamedXContentRegistry xContentRegistry,
            final Environment environment,
            final NodeEnvironment nodeEnvironment,
            final NamedWriteableRegistry namedWriteableRegistry,
            final IndexNameExpressionResolver indexNameExpressionResolver,
            final Supplier<RepositoriesService> repositoriesServiceSupplier
    ) {
        this.client = client;
        return Collections.emptyList();
    }

    @Override
    public List<RestHandler> getRestHandlers(
            final Settings settings,
            final RestController restController,
            final ClusterSettings clusterSettings,
            final IndexScopedSettings indexScopedSettings,
            final SettingsFilter settingsFilter,
            final IndexNameExpressionResolver indexNameExpressionResolver,
            final Supplier<DiscoveryNodes> nodesInCluster
    ) {
        return singletonList(new PhilnderRestHandler(client));
    }

    @Override
    public List<ActionFilter> getActionFilters() {

        try {

            final Properties properties = new Properties();
            final PhileasConfiguration phileasConfiguration = new PhileasConfiguration(properties);
            final PhileasFilterService phileasFilterService = new PhileasFilterService(phileasConfiguration);

            return singletonList(new PhinderActionFilter(phileasFilterService));

        } catch (Exception ex) {
            throw new RuntimeException("Unable to initialize Phileas.", ex);
        }

    }


    @Override
    public List<SearchExtSpec<?>> getSearchExts() {

        final List<SearchExtSpec<?>> searchExts = new ArrayList<>();

        searchExts.add(
                new SearchExtSpec<>(PhinderParametersExtBuilder.PHINDER_PARAMETERS_NAME, PhinderParametersExtBuilder::new, PhinderParametersExtBuilder::parse)
        );

        return searchExts;

    }

}
