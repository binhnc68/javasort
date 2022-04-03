package com.javacore.comparatorN;

public class sortsMap {
	@GET
    @Path("/allspacebaselanguage")
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getAllSpaceLanguage() {
        JsonObject result = new JsonObject();
        try {
            SpaceManager spaceManager = TranslationMemoryToolFactory.getSpaceManager();
            LanguageService languageService = TranslationMemoryToolFactory.getLanguageService(); 
            List<Language> listAllLanguage = languageService.getAll();
            Map<String, Map<String, String>> mapSpaceKey = new HashMap<>();
            for (Language language : listAllLanguage) {
                Language baselanguage = languageService.getBaseLanguage(language.getTranslationKey(), language.getVersion());
                if (baselanguage == null) {
                    continue;
                }
                String baseSpaceKey = baselanguage.getSpaceKey();
                Space spaceBaseLang = spaceManager.getSpace(baseSpaceKey);
                if (spaceBaseLang == null) {
                    continue;
                }
                
            Map<String, String> obj = new HashMap<String, String>();
                obj.put("key", baseSpaceKey);
                obj.put("title", StringEscapeUtils.escapeHtml(spaceBaseLang.getDisplayTitle()));
                obj.put("ishonyakukanri", "1");
                mapSpaceKey.put(baseSpaceKey, obj);
            }
            // get list spacekey manual
            Object objManual = DocumentKanriConnector.execute(DocumentKanriConnector.METHOD_GET_LIST_ALL_MANUAL_VERSION_LAST);
            if (objManual != null) {
                List<String> listManual = (ArrayList) objManual;
                for (String spaceKeyManual : listManual) {
                    if (spaceKeyManual != null) {
                        if (mapSpaceKey.containsKey(spaceKeyManual)) {
                            continue;
                        }
                        Space spaceBaseLangManual = spaceManager.getSpace(spaceKeyManual);
                        if (spaceBaseLangManual == null) {
                            continue;
                        }

                        Map<String, String> obj = new HashMap<String, String>();
                        obj.put("key", spaceKeyManual);
                        obj.put("title", StringEscapeUtils.escapeHtml(spaceBaseLangManual.getDisplayTitle()));
                        obj.put("ishonyakukanri", "0");
                        mapSpaceKey.put(spaceKeyManual, obj);
                    }
                }
            }
            // sort by title
            List<Map.Entry<String, Map<String, String>>> sorts = new ArrayList<>(mapSpaceKey.entrySet());
            Collections.sort(sorts, new Comparator<Map.Entry<String, Map<String, String>>>() {
                @Override
                public int compare(Map.Entry<String, Map<String, String>> o1, Map.Entry<String, Map<String, String>> o2) {
                    return o1.getValue().get("title").compareToIgnoreCase(o2.getValue().get("title"));
                }
            });

            JsonArray array = new JsonArray();
            Gson parser = new Gson();
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            for (Map.Entry<String, Map<String, String>> sort : sorts) {
                JsonElement obj = parser.toJsonTree(sort.getValue(), type);
                array.add(obj);
            }
            result.addProperty("result", true);
            result.add("spaces", array);
            return Response.ok(result.toString()).build();
        } catch (Exception e) {
            return Response.ok(this.toExceptionJson(e)).build();
        }
    }
}
