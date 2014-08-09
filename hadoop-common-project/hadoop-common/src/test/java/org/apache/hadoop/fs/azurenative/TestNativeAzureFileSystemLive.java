package org.apache.hadoop.fs.azurenative;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/*
 * Tests the Native Azure file system (WASB) against an actual blob store if
 * provided in the environment.
 */
public class TestNativeAzureFileSystemLive
    extends NativeAzureFileSystemBaseTest {

  @Override
  protected AzureBlobStorageTestAccount createTestAccount()
      throws Exception {
    return AzureBlobStorageTestAccount.create();
  }

  /**
   * Check that isPageBlobKey works as expected. This assumes that
   * in the test configuration, the list of supported page blob directories
   * only includes "pageBlobs". That's why this test is made specific
   * to this subclass.
   */
  @Test
  public void testIsPageBlobKey() {
    AzureNativeFileSystemStore store = ((NativeAzureFileSystem) fs).getStore();

    // Use literal strings so it's easier to understand the tests.
    // In case the constant changes, we want to know about it so we can update this test.
    assertEquals(AzureBlobStorageTestAccount.DEFAULT_PAGE_BLOB_DIRECTORY, "pageBlobs");

    // URI prefix for test environment.
    String uriPrefix = "file:///";

    // negative tests
    String[] negativeKeys = { "", "/", "bar", "bar/", "bar/pageBlobs", "bar/pageBlobs/foo",
        "bar/pageBlobs/foo/", "/pageBlobs/", "/pageBlobs", "pageBlobs", "pageBlobsxyz/" };
    for (String s : negativeKeys) {
      assertFalse(store.isPageBlobKey(s));
      assertFalse(store.isPageBlobKey(uriPrefix + s));
    }

    // positive tests
    String[] positiveKeys = { "pageBlobs/", "pageBlobs/foo/", "pageBlobs/foo/bar/" };
    for (String s : positiveKeys) {
      assertTrue(store.isPageBlobKey(s));
      assertTrue(store.isPageBlobKey(uriPrefix + s));
    }
  }

  /**
   * Test that isAtomicRenameKey() works as expected.
   */
  @Test
  public void testIsAtomicRenameKey() {

    AzureNativeFileSystemStore store = ((NativeAzureFileSystem) fs).getStore();

    // We want to know if the default configuration changes so we can fix
    // this test.
    assertEquals(AzureBlobStorageTestAccount.DEFAULT_ATOMIC_RENAME_DIRECTORIES,
        "/atomicRenameDir1,/atomicRenameDir2");

    // URI prefix for test environment.
    String uriPrefix = "file:///";

    // negative tests
    String[] negativeKeys = { "", "/", "bar", "bar/", "bar/hbase",
        "bar/hbase/foo", "bar/hbase/foo/", "/hbase/", "/hbase", "hbase",
        "hbasexyz/", "foo/atomicRenameDir1/"};
    for (String s : negativeKeys) {
      assertFalse(store.isAtomicRenameKey(s));
      assertFalse(store.isAtomicRenameKey(uriPrefix + s));
    }

    // Positive tests. The directories for atomic rename are /hbase
    // plus the ones in the configuration (DEFAULT_ATOMIC_RENAME_DIRECTORIES
    // for this test).
    String[] positiveKeys = { "hbase/", "hbase/foo/", "hbase/foo/bar/",
        "atomicRenameDir1/foo/", "atomicRenameDir2/bar/"};
    for (String s : positiveKeys) {
      assertTrue(store.isAtomicRenameKey(s));
      assertTrue(store.isAtomicRenameKey(uriPrefix + s));
    }
  }
}
