package microsoft.graph.generated.entity;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.davidmoten.odata.client.ODataEntity;
import com.github.davidmoten.odata.client.RequestOptions;
import com.github.davidmoten.odata.client.Util;
import com.github.davidmoten.odata.client.internal.ChangedFields;
import com.github.davidmoten.odata.client.internal.RequestHelper;
import com.github.davidmoten.odata.client.internal.UnmappedFields;
import java.time.OffsetDateTime;
import java.util.Optional;
import microsoft.graph.generated.entity.Attachment;
import microsoft.graph.generated.schema.SchemaInfo;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({
    "@odata.type", 
    "contentId", 
    "contentLocation", 
    "contentBytes"})
public class FileAttachment extends Attachment implements ODataEntity {

    @JsonProperty("contentId")
    protected String contentId;

    @JsonProperty("contentLocation")
    protected String contentLocation;

    @JsonProperty("contentBytes")
    protected byte[] contentBytes;

    protected FileAttachment() {
        super();
    }

    public static Builder builderFileAttachment() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private OffsetDateTime lastModifiedDateTime;
        private String name;
        private String contentType;
        private Integer size;
        private Boolean isInline;
        private String contentId;
        private String contentLocation;
        private byte[] contentBytes;
        private ChangedFields changedFields = ChangedFields.EMPTY;

        Builder() {
            // prevent instantiation
        }

        public Builder id(String id) {
            this.id = id;
            this.changedFields = changedFields.add("id");
            return this;
        }

        public Builder lastModifiedDateTime(OffsetDateTime lastModifiedDateTime) {
            this.lastModifiedDateTime = lastModifiedDateTime;
            this.changedFields = changedFields.add("lastModifiedDateTime");
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            this.changedFields = changedFields.add("name");
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            this.changedFields = changedFields.add("contentType");
            return this;
        }

        public Builder size(Integer size) {
            this.size = size;
            this.changedFields = changedFields.add("size");
            return this;
        }

        public Builder isInline(Boolean isInline) {
            this.isInline = isInline;
            this.changedFields = changedFields.add("isInline");
            return this;
        }

        public Builder contentId(String contentId) {
            this.contentId = contentId;
            this.changedFields = changedFields.add("contentId");
            return this;
        }

        public Builder contentLocation(String contentLocation) {
            this.contentLocation = contentLocation;
            this.changedFields = changedFields.add("contentLocation");
            return this;
        }

        public Builder contentBytes(byte[] contentBytes) {
            this.contentBytes = contentBytes;
            this.changedFields = changedFields.add("contentBytes");
            return this;
        }

        public FileAttachment build() {
            FileAttachment _x = new FileAttachment();
            _x.contextPath = null;
            _x.changedFields = changedFields;
            _x.unmappedFields = UnmappedFields.EMPTY;
            _x.odataType = "microsoft.graph.fileAttachment";
            _x.id = id;
            _x.lastModifiedDateTime = lastModifiedDateTime;
            _x.name = name;
            _x.contentType = contentType;
            _x.size = size;
            _x.isInline = isInline;
            _x.contentId = contentId;
            _x.contentLocation = contentLocation;
            _x.contentBytes = contentBytes;
            return _x;
        }
    }

    public ChangedFields getChangedFields() {
        return changedFields;
    }

    public Optional<String> getContentId() {
        return Optional.ofNullable(contentId);
    }

    public FileAttachment withContentId(String contentId) {
        FileAttachment _x = new FileAttachment();
        _x.contextPath = contextPath;
        _x.changedFields = changedFields.add("contentId");
        _x.unmappedFields = unmappedFields;
        _x.odataType = Util.nvl(odataType, "microsoft.graph.fileAttachment");
        _x.id = id;
        _x.lastModifiedDateTime = lastModifiedDateTime;
        _x.name = name;
        _x.contentType = contentType;
        _x.size = size;
        _x.isInline = isInline;
        _x.contentId = contentId;
        _x.contentLocation = contentLocation;
        _x.contentBytes = contentBytes;
        return _x;
    }

    public Optional<String> getContentLocation() {
        return Optional.ofNullable(contentLocation);
    }

    public FileAttachment withContentLocation(String contentLocation) {
        FileAttachment _x = new FileAttachment();
        _x.contextPath = contextPath;
        _x.changedFields = changedFields.add("contentLocation");
        _x.unmappedFields = unmappedFields;
        _x.odataType = Util.nvl(odataType, "microsoft.graph.fileAttachment");
        _x.id = id;
        _x.lastModifiedDateTime = lastModifiedDateTime;
        _x.name = name;
        _x.contentType = contentType;
        _x.size = size;
        _x.isInline = isInline;
        _x.contentId = contentId;
        _x.contentLocation = contentLocation;
        _x.contentBytes = contentBytes;
        return _x;
    }

    public Optional<byte[]> getContentBytes() {
        return Optional.ofNullable(contentBytes);
    }

    public FileAttachment withContentBytes(byte[] contentBytes) {
        FileAttachment _x = new FileAttachment();
        _x.contextPath = contextPath;
        _x.changedFields = changedFields.add("contentBytes");
        _x.unmappedFields = unmappedFields;
        _x.odataType = Util.nvl(odataType, "microsoft.graph.fileAttachment");
        _x.id = id;
        _x.lastModifiedDateTime = lastModifiedDateTime;
        _x.name = name;
        _x.contentType = contentType;
        _x.size = size;
        _x.isInline = isInline;
        _x.contentId = contentId;
        _x.contentLocation = contentLocation;
        _x.contentBytes = contentBytes;
        return _x;
    }

    @JsonAnySetter
    private void setUnmappedField(String name, Object value) {
        if (unmappedFields == null) {
            unmappedFields = new UnmappedFields();
        }
        unmappedFields.put(name, value);
    }

    @Override
    public UnmappedFields getUnmappedFields() {
        return unmappedFields == null ? UnmappedFields.EMPTY : unmappedFields;
    }

    public FileAttachment patch() {
        RequestHelper.patch(this, contextPath, RequestOptions.EMPTY,  SchemaInfo.INSTANCE);
        // pass null for changedFields to reset it
        FileAttachment _x = new FileAttachment();
        _x.contextPath = contextPath;
        _x.changedFields = null;
        _x.unmappedFields = unmappedFields;
        _x.odataType = odataType;
        _x.id = id;
        _x.lastModifiedDateTime = lastModifiedDateTime;
        _x.name = name;
        _x.contentType = contentType;
        _x.size = size;
        _x.isInline = isInline;
        _x.contentId = contentId;
        _x.contentLocation = contentLocation;
        _x.contentBytes = contentBytes;
        return _x;
    }

    public FileAttachment put() {
        RequestHelper.put(this, contextPath, RequestOptions.EMPTY,  SchemaInfo.INSTANCE);
        // pass null for changedFields to reset it
        FileAttachment _x = new FileAttachment();
        _x.contextPath = contextPath;
        _x.changedFields = null;
        _x.unmappedFields = unmappedFields;
        _x.odataType = odataType;
        _x.id = id;
        _x.lastModifiedDateTime = lastModifiedDateTime;
        _x.name = name;
        _x.contentType = contentType;
        _x.size = size;
        _x.isInline = isInline;
        _x.contentId = contentId;
        _x.contentLocation = contentLocation;
        _x.contentBytes = contentBytes;
        return _x;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("FileAttachment[");
        b.append("id=");
        b.append(this.id);
        b.append(", ");
        b.append("lastModifiedDateTime=");
        b.append(this.lastModifiedDateTime);
        b.append(", ");
        b.append("name=");
        b.append(this.name);
        b.append(", ");
        b.append("contentType=");
        b.append(this.contentType);
        b.append(", ");
        b.append("size=");
        b.append(this.size);
        b.append(", ");
        b.append("isInline=");
        b.append(this.isInline);
        b.append(", ");
        b.append("contentId=");
        b.append(this.contentId);
        b.append(", ");
        b.append("contentLocation=");
        b.append(this.contentLocation);
        b.append(", ");
        b.append("contentBytes=");
        b.append(this.contentBytes);
        b.append("]");
        b.append(",unmappedFields=");
        b.append(unmappedFields);
        b.append(",odataType=");
        b.append(odataType);
        return b.toString();
    }
}
