package com.ebious.pdf;

import com.ebious.pdf.domain.dto.NamespaceDto;
import com.ebious.pdf.domain.entity.Filename;
import com.ebious.pdf.domain.entity.Namespace;

import java.util.Arrays;
import java.util.List;

public class TestData {
    public final static Namespace expectedNamespace1 = new Namespace("namespace_1");
    public final static Namespace expectedNamespace2 = new Namespace("namespace_2");
    public final static Namespace expectedNamespace3 = new Namespace("namespace_3");
    public final static Namespace expectedNamespaceWithoutFilenames = new Namespace("namespace_without_filenames");
    public final static Filename expected_filename1_Namespace1 = new Filename(1L, "filename_1_ns_1");
    public final static Filename expected_filename2_Namespace1 = new Filename(2L, "filename_2_ns_1");
    public final static Filename expected_filename1_Namespace2 = new Filename(3L, "filename_1_ns_2");
    public final static Filename expected_filename2_Namespace2 = new Filename(4L, "filename_2_ns_2");
    public final static Filename expected_filename1_Namespace3 = new Filename(5L, "filename_1_ns_3");
    public final static Filename expected_filename2_Namespace3 = new Filename(6L, "filename_2_ns_3");

    public final static List<String> batchFilenamesOk = Arrays.asList("filename_1", "filename_2", "filename_3");
    public final static List<String> batchFilenamesFailed = Arrays.asList("filename_1", "filename_2", "filename_2");

    public final static Namespace newNamespace = new Namespace("new_namespace");
    public final static Filename newFilename = new Filename(7L, "new_filename_new_namespace");
    public final static Namespace notExistNamespace = new Namespace("not_exist_namespace");
    public final static Filename notExistFilename = new Filename(8L, "not_exist_filename");

    public final static NamespaceDto jsonNamespaceDto1 = new NamespaceDto("Namespace_1",
            Arrays.asList("filename_1_to_namespace_1", "filename_2_to_namespace_1", "filename_3_to_namespace_1"));
    public final static NamespaceDto jsonNamespaceDto2 = new NamespaceDto("Namespace_2",
            Arrays.asList("filename_1_to_namespace_2", "filename_2_to_namespace_2", "filename_3_to_namespace_2"));
    public final static NamespaceDto jsonNamespaceDto3 = new NamespaceDto("Namespace_3",
            Arrays.asList("filename_1_to_namespace_3", "filename_2_to_namespace_3", "filename_3_to_namespace_3"));
}
