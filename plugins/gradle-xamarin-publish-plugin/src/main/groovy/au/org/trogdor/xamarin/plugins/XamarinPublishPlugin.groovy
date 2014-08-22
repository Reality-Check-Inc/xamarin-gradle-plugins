package au.org.trogdor.xamarin.plugins

import au.org.trogdor.xamarin.lib.XamarinProject
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.XmlProvider
import org.gradle.api.internal.UserCodeAction
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.listener.ActionBroadcast

class XamarinPublishPlugin implements Plugin<Project> {
	void apply(Project project) {
        project.extensions.create("xamarinPublish", XamarinPublishExtension, project)
        project.plugins.apply('maven-publish')
        ((ProjectInternal)project).getConfigurationActions().add(new Action<ProjectInternal>() {
            @java.lang.Override
            void execute(ProjectInternal projectInternal) {
                XamarinProject xamarinProject = projectInternal.xamarin.xamarinProject
                def resolvedArtifactId = projectInternal.xamarinPublish.artifactId ?: xamarinProject.resolvedProjectName

                MavenPublication publication = projectInternal.publishing.publications.create('xamarin', MavenPublication)
                publication.artifactId = resolvedArtifactId
                publication.pom.withXml(projectInternal.xamarinPublish.getXmlAction())

                xamarinProject.configurations.all {configuration->
                    addArtifacts(configuration, publication, projectInternal)
                }
                projectInternal.tasks.publishToMavenLocal.dependsOn('buildAll')
                projectInternal.tasks.publish.dependsOn('buildAll')
            }

            private void addArtifacts(configuration, MavenPublication publication, ProjectInternal projectInternal) {
                def classifierName = configuration.name.toLowerCase()
                projectInternal.logger.info("Adding artifacts to publication '${publication.name}' with classifier '${classifierName}'")
                configuration.resolvedBuildOutput.with {
                    if (projectInternal.file(it).exists())
                        projectInternal.logger.info("Artifact with classifier '${classifierName}': ${projectInternal.file(it).canonicalPath}")
                        publication.artifact(it) {
                            extension "dll"
                            classifier classifierName
                        }

                    def symbolsPath = "${it}.mdb"
                    if (projectInternal.file(symbolsPath).exists()) {
                        projectInternal.logger.info("Artifact with classifier '${classifierName}': ${projectInternal.file(symbolsPath).canonicalPath}")
                        publication.artifact(symbolsPath) {
                            extension "dll.mdb"
                            classifier "$classifierName-symbols"
                        }
                    }
                }
            }
        })
    }
}

class XamarinPublishExtension {
    final def Project project
    private def String mArtifactId

    private final ActionBroadcast<XmlProvider> xmlAction = new ActionBroadcast<XmlProvider>();

    XamarinPublishExtension(Project project) {
        this.project = project
    }

    void artifactId(String artifactId) {
        mArtifactId = artifactId
    }

    String getArtifactId() {
        mArtifactId
    }

    public void withPomXml(Action<? super XmlProvider> action) {
        xmlAction.add(new UserCodeAction<XmlProvider>("Could not apply withXml() to generated POM", action));
    }

    public Action<XmlProvider> getXmlAction() {
        return xmlAction;
    }
}
