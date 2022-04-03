package com.javacore.comparatorN;

public class renderTableWithPaging {
	public String renderTableWithPaging() throws Exception {
        this.resultBean = new HashMap<String, Object>();
        try {
            ActionContext context = ActionContext.getContext();
            spaceKey = ActionContextHelper.getFirstParameterValueAsString(context, "spaceKey");
            languageKey = ActionContextHelper.getFirstParameterValueAsString(context, "languageKey");
            int pageSize = ActionContextHelper.getFirstParameterValueAsInt(context, "pageSize", 0);
            int startIndex = ActionContextHelper.getFirstParameterValueAsInt(context, "index", 1);
            String listJsonString = ActionContextHelper.getFirstParameterValueAsString(context, "dataJsonClone");
            String dataJsonRemoveString = ActionContextHelper.getFirstParameterValueAsString(context, "dataJsonRemove");
            if (dataJsonRemoveString == null) {
                dataJsonRemoveString = "[]";
            }
            Gson gson = new Gson();
            Type type = new TypeToken<List<ConvertTermStringObject>>() {
            }.getType();
            List<ConvertTermStringObject> dataJsonRemove = gson.fromJson(dataJsonRemoveString, type);

            ConvertTermStringService convertTermStringService = ConvertTermStringFactory.getConvertTermStringService();
            convertTermStrings = convertTermStringService.getAllConvertTermStrings(languageKey);

            List<ConvertTermStringObject> convertTermStringsClone = gson.fromJson(listJsonString, type);

            Collections.sort(convertTermStrings, new Comparator<ConvertTermString>() {
                @Override
                public int compare(ConvertTermString c1, ConvertTermString c2) {
                    return c2.getDate().compareTo(c1.getDate());
                }
            });
            for (ConvertTermString convertTermString : convertTermStrings) {
                ConvertTermStringObject convertTermStringObj = new ConvertTermStringObject();
                convertTermStringObj.setBeforeText(convertTermString.getBeforeText());
                convertTermStringObj.setAfterText(convertTermString.getAfterText());
                convertTermStringObj.setChecked(convertTermString.getChecked());
                convertTermStringObj.setClassName("");
                if (containsConvertTermStringObject(dataJsonRemove, convertTermString.getBeforeText())) {
                    convertTermStringObj.setClassName("is_remove");
                }
                convertTermStringsClone.add(convertTermStringObj);
            }
            int size = (convertTermStringsClone.size() + pageSize - 1) / pageSize;
            convertTermStringsClone = getListPaging(startIndex, pageSize, convertTermStringsClone);
            JsonElement convertTermStringsJson = convertListConvertTermStringsToJson(convertTermStringsClone);
            resultBean.put("convertTermStrings", convertTermStringsJson.toString());
            resultBean.put("result", true);
            resultBean.put("totalSize", size);
        } catch (Exception e) {
            ConvertTermStringLogUtil.outputLogException(this.getClass(), e);
            this.resultBean = ConvertTermStringLogUtil.getBeanFaild(e);
        }

        return SUCCESS;
    }
	
	@Override
    public String doExport(ProgressMeter progress) throws ImportExportException {
        try {
            this.progress = progress;
            userContentBean = null;

            initSettingBean();
            int entSize = getWorkingExportContext().getWorkingEntities().size();
            this.countPage = getWorkingExportContext().getContentTree().getAllContentNodes().size();
            if (this.countPage < 1) {
                this.countPage = 1;
            }

            // for export multispace
            if (entSize == 2) {
                getWorkingExportContext().getWorkingEntities().remove(0);
            }
            if (this.baseExportPath.equals("") || this.baseExportPath == null) {
                this.baseExportPath = super.doExport(progress);
            }
            //Choose the file name based on the first entity on the list
            ConfluenceEntityObject firstEntity = getWorkingExportContext().getWorkingEntities().get(0);
            //String archivePath = baseExportPath + File.separator + prepareExportFileName(firstEntity) + ".zip";
            String archivePath = baseExportPath + File.separator + prepareExportFileName(firstEntity);
            String spacekey = "";

            // if hierarchy flag is set for page export, a index page has to be created.
            // Since all pages are in the same space, the space to create the path is taken from the root page.
            // In order to have all the children pages, we clone the root entity
            Page indexPage = null;
            if (context.isExportHierarchy() && firstEntity instanceof Page) {
                try {
                    indexPage = (Page) firstEntity.clone();
                } catch (CloneNotSupportedException e) {
                    log.error("Can't clone page?!", e);
                    ExportHTMLLogUtil.outputLogException(this.getClass(), e);
                    throw new InfrastructureException("Can't clone content entity object: " + indexPage, e);
                } catch (OutOfMemoryError oe) {
                    ExportHTMLLogUtil.outputLogException(this.getClass(), oe);
                    throw new ImportExportException(oe);
                }
                indexPage.setTitle("Index");
                indexPage.setChildren(new ArrayList<Page>(1));
                indexPage.getChildren().add((Page) firstEntity);
                try {
                    exportPage(indexPage, baseExportPath, 0L, 0, UserExportThemeConfig.PC_MODE);
                } catch (Exception ex) {
                    throw new ImportExportException(ex);
                } catch (OutOfMemoryError oe) {
                    ExportHTMLLogUtil.outputLogException(this.getClass(), oe);
                    throw new ImportExportException(oe);
                }
            }

            if (firstEntity instanceof Page) {
                try {
                    indexPage = (Page) firstEntity.clone();
                    spacekey = indexPage.getSpaceKey();
                } catch (CloneNotSupportedException e) {
                    log.error("Can't clone page?!", e);
                    ExportHTMLLogUtil.outputLogException(this.getClass(), e);
                    throw new InfrastructureException("Can't clone content entity object: " + indexPage, e);
                } catch (OutOfMemoryError oe) {
                    ExportHTMLLogUtil.outputLogException(this.getClass(), oe);
                    throw new ImportExportException(oe);
                }
            }
            if (firstEntity instanceof Space) {
                spacekey = ((Space) firstEntity).getKey();
            }

            if (spaceCnt != 0) {
                if (!IsGrossSpace()) {
                    exportSpace(spaceManager.getSpace(spacekey), baseExportPath);
                }
            }

//            //検索用jsの出力
//            StringBuilder jsPath = new StringBuilder(baseExportPath);
//            jsPath.append(File.separator).append(GrossSpaceDir(firstEntity)).append(File.separator).append(HTML_DIR).append(File.separator);
//            SearchTargets.End(jsPath.toString());
//            new SpaceSearchData().WriteData(jsPath.toString(), (Space) firstEntity, 0);
            // zipファイルの作成
            //FileUtils.createZipFile(new File(baseExportPath), new File(zipPath));
            List<File> listFile = (List) org.apache.commons.io.FileUtils.listFiles(
                    new File(this.baseExportPath),
                    new RegexFileFilter("^((?!index|toc).)*[.]html$"),
                    DirectoryFileFilter.DIRECTORY
            );

            Boolean isAddPrefix = this.themeConfig.getValueAsBoolean("prefixLink");
            for (File file : listFile) {
                DeleteHtmlElementUtil.replaceLinkAnchorFile(file, indexPage == null ? "0" : indexPage.getIdAsString(), mapAnchorPage);
                if (isAddPrefix == true) {
                    DeleteHtmlElementUtil.replaceLinkAddPrefix(file, indexPage == null ? "0" : indexPage.getIdAsString(), this.mapPrefixLink);
                }
            }

            StringBuilder exportPath = new StringBuilder(baseExportPath);
            exportPath.append(File.separator).append(GrossSpaceDir(firstEntity)).append(File.separator);
            if (this.themeConfig.getConfig().has("pathReplaceTemplate")) {
                Map<String, String> mapReplace = this.themeConfig.getValueOfT("pathReplaceTemplate");
                if (mapReplace.isEmpty() == false) {
                    List<Map.Entry<String, String>> lstEntry = new ArrayList<Map.Entry<String, String>>(mapReplace.entrySet());
                    Collections.sort(lstEntry, new Comparator<Map.Entry<String, String>>() {
                        @Override
                        public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                            if (o1.getValue() == null || o1.getValue().isEmpty() == true) {
                                return 1;
                            }

                            if (o2.getValue() == null || o2.getValue().isEmpty() == true) {
                                return -1;
                            }

                            File file1 = new File(o1.getKey());
                            File file2 = new File(o2.getKey());
                            String extension1 = FilenameUtils.getExtension(file1.getName());
                            String extension2 = FilenameUtils.getExtension(file2.getName());
                            boolean isFile1 = extension1 != null && extension1.isEmpty() == false;
                            boolean isFile2 = extension2 != null && extension2.isEmpty() == false;
                            if (isFile1 == false && isFile2 == true) {
                                return 1;
                            }

                            if (isFile1 == true && isFile2 == false) {
                                return -1;
                            }

                            String[] l1 = file1.getPath().split("\\" + File.separator);
                            String[] l2 = file2.getPath().split("\\" + File.separator);
                            return Integer.compare(l2.length, l1.length);
                        }
                    });

                    List<File> listFileReplace = (List) org.apache.commons.io.FileUtils.listFiles(
                            new File(exportPath.toString()),
                            new RegexFileFilter("^((.+?).)*[.](html|css|js)$"),
                            DirectoryFileFilter.DIRECTORY);
                    for (File file : listFileReplace) {
                        if (FilenameUtils.getExtension(file.getName()).equals("html")) {
                            DeleteHtmlElementUtil.replaceHtmlFileContent(exportPath.toString(), file, lstEntry);
                        } else {
                            DeleteHtmlElementUtil.replaceLinkFiles(exportPath.toString(), file, lstEntry);
                        }
                    }
                    FileUtility.renameAndDelete(lstEntry, new File(exportPath.toString()));
                }
            }

            Map<String, String> customHtml = this.themeConfig.getValueOfT("customHtml");
            if (customHtml != null && customHtml.isEmpty() == false) {
                List<File> listFileReplace = (List) org.apache.commons.io.FileUtils.listFiles(
                        new File(exportPath.toString()),
                        new RegexFileFilter("^((.+?).)*[.](htm|html)$"),
                        DirectoryFileFilter.DIRECTORY);
                for (File file : listFileReplace) {
                    DeleteHtmlElementUtil.addCustomPageSettings(file, customHtml);
                }
            }

            spaceCnt++;
            return archivePath;
        } catch (Exception e) {
            ExportHTMLLogUtil.outputLogException(this.getClass(), e);
            throw new ImportExportException(e);
        } catch (OutOfMemoryError oe) {
            ExportHTMLLogUtil.outputLogException(this.getClass(), oe);
            throw new ImportExportException(oe);
        }
    }
}
