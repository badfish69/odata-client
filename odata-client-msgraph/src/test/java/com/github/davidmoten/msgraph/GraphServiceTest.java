package com.github.davidmoten.msgraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import com.github.davidmoten.guavamini.Sets;
import com.github.davidmoten.odata.client.CollectionPage;
import com.github.davidmoten.odata.client.HttpMethod;
import com.github.davidmoten.odata.client.PathStyle;
import com.github.davidmoten.odata.client.RequestHeader;
import com.github.davidmoten.odata.client.TestingService.ContainerBuilder;

import odata.msgraph.client.complex.Identity;
import odata.msgraph.client.complex.IdentitySet;
import odata.msgraph.client.complex.InvitationParticipantInfo;
import odata.msgraph.client.complex.ServiceHostedMediaConfig;
import odata.msgraph.client.container.GraphService;
import odata.msgraph.client.entity.Attachment;
import odata.msgraph.client.entity.Call;
import odata.msgraph.client.entity.Contact;
import odata.msgraph.client.entity.DriveItem;
import odata.msgraph.client.entity.FileAttachment;
import odata.msgraph.client.entity.ItemAttachment;
import odata.msgraph.client.entity.Message;
import odata.msgraph.client.entity.User;
import odata.msgraph.client.enums.Importance;
import odata.msgraph.client.enums.Modality;

public class GraphServiceTest {

    @Test
    public void testFileAttachmentBuilderCompiles() {
        FileAttachment.builderFileAttachment().build();
    }

    @Test
    public void testGetEntityWithComplexTypeCollection() {
        GraphService client = createClient("/users/1", "/response-user.json", //
                RequestHeader.ODATA_VERSION, //
                RequestHeader.ACCEPT_JSON_METADATA_MINIMAL);
        User user = client.users("1").get();
        assertEquals("Conf Room Adams", user.getDisplayName().get());
        assertEquals(1, user.getBusinessPhones().currentPage().size());
        assertEquals("+61 2 1234567", user.getBusinessPhones().currentPage().get(0));
    }

    @Test
    public void testGetEntityCollectionWithoutNextPage() {
        GraphService client = createClient("/users", "/response-users.json",
                RequestHeader.ACCEPT_JSON_METADATA_MINIMAL, RequestHeader.ODATA_VERSION);
        assertNotNull(client.users().get());
        CollectionPage<User> c = client.users().get();
        assertNotNull(c);
        assertEquals(31, c.currentPage().size());
        assertFalse(c.nextPage().isPresent());
    }

    @Test
    public void testGetEntityCollectionWithNextPage() {
        GraphService client = clientBuilder() //
                .expectResponse("/me/contacts", "/response-contacts.json",
                        RequestHeader.ACCEPT_JSON_METADATA_MINIMAL, RequestHeader.ODATA_VERSION) //
                // TODO what request header should be specified for next page?
                .expectResponse("/me/contacts?$skip=10", "/response-contacts-next-page.json",
                        RequestHeader.ACCEPT_JSON_METADATA_MINIMAL, RequestHeader.ODATA_VERSION) //
                .build();
        CollectionPage<Contact> c = client.me().contacts().get();
        assertNotNull(c);
        assertEquals(10, c.currentPage().size());
        assertTrue(c.nextPage().isPresent());
        c = c.nextPage().get();
        assertEquals(10, c.currentPage().size());
        assertEquals("Justin", c.currentPage().get(9).getGivenName().get());
    }

    @Test
    public void testGetEntityWithNestedComplexTypesAndEnumDeserialisationAndUnmappedFields() {
        GraphService client = createClient("/me/messages/1", "/response-message.json",
                RequestHeader.ODATA_VERSION, //
                RequestHeader.ACCEPT_JSON_METADATA_MINIMAL);
        Message m = client.me().messages("1").get();
        assertTrue(m.getSubject().get().startsWith("MyAnalytics"));
        assertEquals("MyAnalytics", m.getFrom().get().getEmailAddress().get().getName().get());
        assertEquals(Importance.NORMAL, m.getImportance().get());
        assertEquals(Sets.newHashSet("@odata.etag", "@odata.context"),
                m.getUnmappedFields().keySet());
        assertEquals("W/\"CQAAABYAAAAiIsqMbYjsT5e/T7KzowPTAAEMTBu8\"",
                m.getUnmappedFields().get("@odata.etag"));
        assertEquals(
                "https://graph.microsoft.com/v1.0/$metadata#users('48d31887-5fad-4d73-a9f5-3c356e68a038')/messages/$entity",
                m.getUnmappedFields().get("@odata.context"));
    }

    @Test
    public void testEntityCollectionNotFromEntityContainer() {
        GraphService client = createClient("/me/messages/1/attachments",
                "/response-me-messages-1-attachments.json",
                RequestHeader.ACCEPT_JSON_METADATA_MINIMAL, RequestHeader.ODATA_VERSION);
        List<Attachment> list = client.me().messages("1").attachments().get().toList();
        assertEquals(16, list.size());
    }

    @Test
    public void testDeserializationOfAttachmentEntityReturnsFileAttachment() {
        GraphService client = createClient("/me/messages/1/attachments/2",
                "/response-attachment.json", RequestHeader.ODATA_VERSION, //
                RequestHeader.ACCEPT_JSON_METADATA_MINIMAL);
        Attachment m = client.me().messages("1").attachments("2").get();
        assertTrue(m instanceof FileAttachment);
        FileAttachment f = (FileAttachment) m;
        assertEquals(6762, f.getContentBytes().get().length);
        assertEquals("lamp_thin.png", f.getContentId().get());
    }
    
    @Test
    public void testGetNestedCollectionWhichTestsContextPathSetWithIdInFirstCollection() {
        GraphService client = clientBuilder() //
                .expectResponse(
                        "/users/fred/mailFolders/inbox/messages?$filter=isRead%20eq%20false&$orderBy=createdDateTime",
                        "/response-messages-expand-attachments-minimal-metadata.json",
                        RequestHeader.ACCEPT_JSON_METADATA_MINIMAL, RequestHeader.ODATA_VERSION) //
                .expectResponse(
                        "/users/fred/mailFolders/inbox/messages/AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OABGAAAAAAAiQ8W967B7TKBjgx9rVEURBwAiIsqMbYjsT5e-T7KzowPTAAAAAAEJAAAiIsqMbYjsT5e-T7KzowPTAAAYbvZDAAA%3D/attachments",
                        "/response-message-attachments.json",
                        RequestHeader.ACCEPT_JSON_METADATA_MINIMAL, RequestHeader.ODATA_VERSION) //
                .build();
        Message m = client //
                .users("fred") //
                .mailFolders("inbox") //
                .messages() //
                .filter("isRead eq false") //
                .orderBy("createdDateTime") //
                .metadataMinimal() //
                .iterator() //
                .next();
        assertEquals(Arrays.asList("lamp_thin.png"), m.getAttachments().stream()
                .map(x -> x.getName().orElse("?")).collect(Collectors.toList()));
    }

    @Test
    public void testGetStreamOnItemAttachment() throws IOException {
        GraphService client = clientBuilder() //
                .expectResponse(
                        "/users/fred/mailFolders/Inbox/messages?$filter=isRead%20eq%20false&$orderBy=createdDateTime",
                        "/response-messages-with-item-attachment.json",
                        RequestHeader.ACCEPT_JSON_METADATA_MINIMAL, RequestHeader.ODATA_VERSION) //
                .expectResponse("/users/fred/mailFolders/Inbox/messages/86/attachments",
                        "/response-attachments.json", RequestHeader.ACCEPT_JSON_METADATA_FULL,
                        RequestHeader.ODATA_VERSION) //
                .expectResponse(
                        "/users/fred/mailFolders/Inbox/messages/86/attachments/123/%24value",
                        "/response-item-attachment-raw.txt") //
                .build();
        Message m = client //
                .users("fred") //
                .mailFolders("Inbox") //
                .messages() //
                .filter("isRead eq false") //
                .orderBy("createdDateTime") //
                .metadataMinimal() //
                .iterator() //
                .next();
        ItemAttachment a = (ItemAttachment) m //
                .getAttachments() //
                .metadataFull() //
                .stream() //
                .findFirst() //
                .get();
        String s = new String(Util.read(a.getStream().get().get()));
        assertEquals(60, s.length());
    }
    
    @Test
    public void testCollectionTypesHonourInheritance() {
        GraphService client = clientBuilder() //
                .expectResponse("/users/fred/mailFolders/inbox/messages/1",
                        "/response-message-has-item-attachment.json", RequestHeader.ODATA_VERSION,
                        RequestHeader.ACCEPT_JSON_METADATA_FULL) //
                .expectResponse("/users/fred/mailFolders/inbox/messages/1/attachments",
                        "/response-attachments-includes-item.json", RequestHeader.ODATA_VERSION,
                        RequestHeader.ACCEPT_JSON_METADATA_MINIMAL) //
                .build();
        List<Attachment> list = client //
                .users("fred") //
                .mailFolders("inbox") //
                .messages("1") //
                .metadataFull() //
                .get() //
                .getAttachments() //
                .toList();
        assertEquals(2, list.size());
        assertTrue(list.get(0) instanceof ItemAttachment);
        assertTrue(list.get(1) instanceof FileAttachment);
    }
    
    @Test
    public void testCollectionWithDerivedType() {
        GraphService client = clientBuilder() //
                .expectResponse("/users/fred/mailFolders/inbox/messages/1",
                        "/response-message-has-item-attachment.json", RequestHeader.ODATA_VERSION,
                        RequestHeader.ACCEPT_JSON_METADATA_FULL) //
                .expectResponse("/users/fred/mailFolders/inbox/messages/1/attachments/microsoft.graph.itemAttachment",
                        "/response-attachments-one-item.json", RequestHeader.ODATA_VERSION,
                        RequestHeader.ACCEPT_JSON_METADATA_MINIMAL) //
                .build();
        List<ItemAttachment> list = client //
                .users("fred") //
                .mailFolders("inbox") //
                .messages("1") //
                .metadataFull() //
                .get() //
                .getAttachments() //
                .filter(ItemAttachment.class)
                .toList();
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof ItemAttachment);
    }
    
    @Test
    public void testCollectionWithDerivedTypeFilterAvailableInNextBuilder() {
        GraphService client = clientBuilder() //
                .expectResponse("/users/fred/mailFolders/inbox/messages/1",
                        "/response-message-has-item-attachment.json", RequestHeader.ODATA_VERSION,
                        RequestHeader.ACCEPT_JSON_METADATA_FULL) //
                .expectResponse("/users/fred/mailFolders/inbox/messages/1/attachments/microsoft.graph.itemAttachment",
                        "/response-attachments-one-item.json", RequestHeader.ODATA_VERSION,
                        RequestHeader.ACCEPT_JSON_METADATA_NONE) //
                .build();
        List<ItemAttachment> list = client //
                .users("fred") //
                .mailFolders("inbox") //
                .messages("1") //
                .metadataFull() //
                .get() //
                .getAttachments() //
                .metadataNone() //
                .filter(ItemAttachment.class)
                .toList();
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof ItemAttachment);
    }

    @Test
    public void testUnmappedFields() {
        GraphService client = clientBuilder() //
                .expectResponse("/users/fred/mailFolders/inbox/messages/1",
                        "/response-message-has-item-attachment.json", RequestHeader.ODATA_VERSION,
                        RequestHeader.ACCEPT_JSON_METADATA_FULL) //
                .expectResponse("/users/fred/mailFolders/inbox/messages/1/attachments",
                        "/response-attachments-includes-item.json", RequestHeader.ODATA_VERSION,
                        RequestHeader.ACCEPT_JSON_METADATA_MINIMAL) //
                .build();
        Attachment a = client //
                .users("fred") //
                .mailFolders("inbox") //
                .messages("1") //
                .metadataFull() //
                .get() //
                .getAttachments() //
                .stream() //
                .findFirst() //
                .get();
        String editLink = a.getUnmappedFields().get("@odata.editLink").toString();
        assertEquals("editLink1", editLink);
    }

    @Test
    public void testFunctionBoundToCollection() {
        GraphService client = clientBuilder() //
                .expectRequestAndResponse(
                        "/users/fred/mailFolders/inbox/messages/microsoft.graph.delta?$filter=receivedDateTime%2Bge%2B12345&$orderBy=receivedDateTime%2Bdesc",
                        "/request-messages-delta.json", //
                        "/response-messages-delta.json", //
                        HttpMethod.POST, RequestHeader.ACCEPT_JSON_METADATA_MINIMAL,
                        RequestHeader.ODATA_VERSION) //
                .build();
        Message m = client //
                .users("fred") //
                .mailFolders("inbox") //
                .messages() //
                .delta() //
                .filter("receivedDateTime+ge+12345") //
                .orderBy("receivedDateTime+desc") //
                .metadataMinimal() //
                .iterator() //
                .next();
        assertEquals("86", m.getId().get());
    }

    @Test
    public void testMailMove() {
        // TODO get real json to use for this test
        GraphService client = clientBuilder() //
                .expectResponse(
                        "/users/fred/mailFolders/inbox/messages?$filter=isRead%20eq%20false&$orderBy=createdDateTime&$expand=attachments",
                        "/response-messages-expand-attachments-minimal-metadata.json",
                        RequestHeader.ODATA_VERSION, RequestHeader.ACCEPT_JSON_METADATA_MINIMAL) //
                .expectRequestAndResponse(
                        "/users/fred/mailFolders/inbox/messages/AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OABGAAAAAAAiQ8W967B7TKBjgx9rVEURBwAiIsqMbYjsT5e-T7KzowPTAAAAAAEJAAAiIsqMbYjsT5e-T7KzowPTAAAYbvZDAAA%3D/microsoft.graph.move", //
                        "/request-post-action-move.json", //
                        "/response-message-move.json", //
                        HttpMethod.POST, RequestHeader.ODATA_VERSION,
                        RequestHeader.ACCEPT_JSON_METADATA_FULL,
                        RequestHeader.CONTENT_TYPE_JSON_METADATA_MINIMAL) //
                .build();
        Message m = client //
                .users("fred") //
                .mailFolders("inbox") //
                .messages() //
                .filter("isRead eq false") //
                .expand("attachments") //
                .orderBy("createdDateTime") //
                .metadataMinimal() //
                .iterator() //
                .next();
        Message m2 = m.move("Archive").metadataFull().get().value();
        assertEquals(m.getId(), m2.getId());
    }

    @Test
    public void testMailRead() {

        GraphService client = clientBuilder() //
                .expectResponse(
                        "/users/fred/mailFolders/inbox/messages?$filter=isRead%20eq%20false&$orderBy=createdDateTime&$expand=attachments",
                        "/response-messages-expand-attachments-minimal-metadata.json",
                        RequestHeader.ACCEPT_JSON_METADATA_MINIMAL, RequestHeader.ODATA_VERSION) //
                .expectRequest(
                        "/users/fred/mailFolders/inbox/messages/AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OABGAAAAAAAiQ8W967B7TKBjgx9rVEURBwAiIsqMbYjsT5e-T7KzowPTAAAAAAEJAAAiIsqMbYjsT5e-T7KzowPTAAAYbvZDAAA%3D",
                        "/request-patch-message-is-read.json", HttpMethod.PATCH,
                        RequestHeader.CONTENT_TYPE_JSON_METADATA_MINIMAL,
                        RequestHeader.ODATA_VERSION, RequestHeader.ACCEPT_JSON) //
                .build();

        Message m = client //
                .users("fred") //
                .mailFolders("inbox") //
                .messages() //
                .filter("isRead eq false") //
                .expand("attachments") //
                .orderBy("createdDateTime") //
                .metadataMinimal() //
                .get() // ;
                .iterator() //
                .next();

        System.out.println(m.getSubject());
        // mark as read
        m.withIsRead(true).patch();
    }
    
    @Test
    @Ignore
    //TODO implement
    public void testChunkedUpload() {
        GraphService client = clientBuilder() //
                .build();
        DriveItem item = client.drives("123").items("abc").metadataNone().get();
        byte[] bytes = "1234567890".getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        item.putChunkedContent().get().upload(in, bytes.length, 2);
    }

    @Test
    @Ignore
    public void testCallCompiles() {
        GraphService client = clientBuilder().build();
        Identity user = Identity //
                .builder() //
                .displayName("John") //
                .id("blah") //
                .build();
        IdentitySet set = IdentitySet //
                .builder() //
                .user(user) //
                .build();
        InvitationParticipantInfo targets = InvitationParticipantInfo //
                .builder()//
                .identity(set) //
                .build();
        ServiceHostedMediaConfig config = ServiceHostedMediaConfig //
                .builderServiceHostedMediaConfig() //
                .build();
        Call call = Call.builderCall() //
                .callbackUri("https://bot.contoso.com/callback") //
                .targets(Collections.singletonList(targets)) //
                .requestedModalities(Collections.singletonList(Modality.AUDIO)) //
                .mediaConfig(config) //
                .build();
        client.communications().calls().post(call);
    }

    // test paged complex type
    //

    private static ContainerBuilder<GraphService> clientBuilder() {
        return GraphService //
                .test() //
                .baseUrl("https://graph.microsoft.com/v1.0") //
                .pathStyle(PathStyle.IDENTIFIERS_AS_SEGMENTS) //
                .addProperty("modify.stream.edit.link", "true");
    }

    private static GraphService createClient(String path, String resource,
            RequestHeader... requestHeaders) {
        return clientBuilder() //
                .expectResponse(path, resource, requestHeaders) //
                .build();
    }
}
