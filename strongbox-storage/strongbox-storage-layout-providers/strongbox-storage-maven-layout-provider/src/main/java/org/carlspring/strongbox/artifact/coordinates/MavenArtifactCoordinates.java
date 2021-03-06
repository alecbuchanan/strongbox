package org.carlspring.strongbox.artifact.coordinates;

import org.carlspring.strongbox.artifact.MavenArtifact;
import org.carlspring.strongbox.artifact.MavenArtifactUtils;
import org.carlspring.strongbox.artifact.MavenDetachedArtifact;

import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.versioning.ComparableVersion;

/**
 * @author carlspring
 */
import org.apache.maven.artifact.versioning.ComparableVersion;

/**
 * @author carlspring
 */
@Embeddable
@XmlRootElement(name = "maven-artifact-coordinates")
@XmlAccessorType(XmlAccessType.NONE)
@ArtifactLayout("maven")
public class MavenArtifactCoordinates
        extends AbstractArtifactCoordinates<MavenArtifactCoordinates, ComparableVersion>
{

    private static final String GROUPID = "groupId";

    private static final String ARTIFACTID = "artifactId";

    private static final String VERSION = "version";

    private static final String CLASSIFIER = "classifier";

    private static final String EXTENSION = "extension";

    private String groupId;

    private String artifactId;

    private String version;

    private String classifier;

    private String extension = "jar";


    public MavenArtifactCoordinates()
    {
        defineCoordinates(GROUPID, ARTIFACTID, VERSION, CLASSIFIER, EXTENSION);
    }

    public MavenArtifactCoordinates(String path)
    {
        this(MavenArtifactUtils.convertPathToArtifact(path));
    }

    public MavenArtifactCoordinates(String... coordinateValues)
    {
        this();

        int i = 0;
        for (String coordinateValue : coordinateValues)
        {
            // Please, forgive the following construct...
            // (In my defense, I felt equally stupid and bad for doing it this way):
            switch (i)
            {
                case 0:
                    setGroupId(coordinateValue);
                    break;
                case 1:
                    setArtifactId(coordinateValue);
                    break;
                case 2:
                    setVersion(coordinateValue);
                    break;
                case 3:
                    setClassifier(coordinateValue);
                    break;
                case 4:
                    setExtension(coordinateValue);
                    break;
                default:
                    break;
            }

            i++;
        }

        if (extension == null)
        {
            extension = "jar";
        }

    }

    public MavenArtifactCoordinates(MavenArtifact artifact)
    {
        this();

        setGroupId(artifact.getGroupId());
        setArtifactId(artifact.getArtifactId());
        setVersion(artifact.getVersion());
        setClassifier(artifact.getClassifier());
        if (artifact.getPath() != null)
        {
            String extension = artifact.getFile().getAbsolutePath();
            extension = extension.substring(extension.lastIndexOf('.'), extension.length());

            setExtension(extension);
        }
        else if (StringUtils.isNotBlank(artifact.getType()))
        {
            setExtension(artifact.getType());
        }
        else
        {
            setExtension("jar");
        }
    }

    @Override
    public String toPath()
    {
        try
        {
            return MavenArtifactUtils.convertArtifactToPath(toArtifact());
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            return getCoordinates().toString();
        }
    }

    public MavenArtifact toArtifact()
    {
        return new MavenDetachedArtifact(getGroupId(), getArtifactId(), getVersion(), getExtension(), getClassifier());
    }

    @XmlAttribute(name = "groupId")
    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
        setCoordinate(GROUPID, this.groupId);
    }

    @XmlAttribute(name = "artifactId")
    public String getArtifactId()
    {
        return artifactId;
    }

    public void setArtifactId(String artifactId)
    {
        this.artifactId = artifactId;
        setCoordinate(ARTIFACTID, this.artifactId);
    }

    @Override
    public String getId()
    {
        return artifactId;
    }

    @Override
    public void setId(String id)
    {
        setArtifactId(id);
    }

    @Override
    @XmlAttribute(name = "version")
    public String getVersion()
    {
        return version;
    }

    @Override
    public void setVersion(String version)
    {
        this.version = version;
        setCoordinate(VERSION, this.version);
    }

    @XmlAttribute(name = "classifier")
    public String getClassifier()
    {
        return classifier;
    }

    public void setClassifier(String classifier)
    {
        this.classifier = classifier;
        setCoordinate(CLASSIFIER, this.classifier);
    }

    @XmlAttribute(name = "extension")
    public String getExtension()
    {
        return extension;
    }

    public void setExtension(String extension)
    {
        this.extension = extension;
        setCoordinate(EXTENSION, this.extension);
    }

    @Override
    public ComparableVersion getNativeVersion()
    {
        String versionLocal = getVersion();
        if (versionLocal == null)
        {
            return null;
        }
        return new ComparableVersion(versionLocal);
    }

    @Override
    public Map<String, String> dropVersion()
    {
        Map<String, String> result = getCoordinates();
        result.remove(VERSION);
        return result;
    }

    @Override
    public String toString()
    {
        return "MavenArtifactCoordinates{" + "groupId='" + groupId + '\'' + ", artifactId='" + artifactId + '\'' +
               ", version='" + version + '\'' + ", classifier='" + classifier + '\'' + ", extension='" + extension +
               '\'' + ", as path: " + toPath() + '}';
    }
}
