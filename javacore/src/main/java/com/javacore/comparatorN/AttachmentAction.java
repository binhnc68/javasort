package com.javacore.comparatorN;

import java.util.*;
import java.util.stream.Collectors;

public class AttachmentAction {

	private PageManager pageManager;

	private final Map<String, Object> resultMap = new HashMap<>();

	private String attachmentList;
	private String taskId;
	private String sortBy;
	private List<String> sortByMulti;
	private String pagination;
	private String filters;

	private double attachmentCount;
	private String spaceKey;
	private String pageId;

	private static final int PAGE_SIZE = 20;

	@Override
	public Object getBean() {
		resultMap.put("attachmentUsageTaskId", this.taskId);
		resultMap.put("attachmentList", this.attachmentList);
		resultMap.put("sortBy", this.sortBy);
		resultMap.put("pagination", this.pagination);
		resultMap.put("filters", this.filters);
		return resultMap;
	}

	@SuppressWarnings("unused")
	public String doGet() {
		long startTime = System.nanoTime();
		ActionContext context = ActionContext.getContext();
		this.spaceKey = getFirstParameterValueAsString(context, "spaceKey");
		if (this.spaceKey == null || this.spaceKey.trim().isEmpty()) {
			return ERROR;
		}

		this.pageId = getFirstParameterValueAsString(context, "pageId");
		if (this.pageId == null || this.pageId.isEmpty()) {
			return ERROR;
		}
		Page page = pageManager.getPage(Long.parseLong(this.pageId));
		if (page == null) {
			return ERROR;
		}

		this.sortBy = getFirstParameterValueAsString(context, "sortBy");
		if (this.sortBy == null || this.sortBy.isEmpty()) {
			this.sortBy = "name";
		}

		this.sortByMulti = getParameterValuesAsStringList(context, "sortByMulti");
		if (this.sortByMulti == null || this.sortByMulti.isEmpty()) {
			this.sortByMulti = new ArrayList<String>();
			this.sortByMulti.add(this.sortBy);
		}

		int currentPage = ActionContextHelper.getFirstParameterValueAsInt(context, "currentPage", 1);
		boolean hasFilter = getFirstCheckBoxParameterValueAsBoolean(context, "hasFilter");
		boolean isAttachmentByLang = getFirstCheckBoxParameterValueAsBoolean(context, "isAttachmentByLang");
		String langKey = getFirstParameterValueAsString(context, "langKey");
		boolean isBaseLanguange = getFirstCheckBoxParameterValueAsBoolean(context, "isBaseLanguange");

		this.initiateAttachmentUsageJob();

		// detele attachment have status = deleted
		List<Attachment> attachments = page.getAttachments();
		List<Attachment> originalAttachments = new ArrayList<>();
		for (Attachment attachment : attachments) {
			if (attachment.isDeleted() == true) {
				continue;
			}
			originalAttachments.add(attachment);
		}
		Map<String, CustomAttachment> customAttachmentMap = this.attachmentsToMap(originalAttachments,
				isAttachmentByLang, langKey, isBaseLanguange);

		List<CustomAttachment> customAttachmentList = hasFilter
				? this.getFilteredCustomAttachmentList(context, customAttachmentMap, currentPage)
				: this.getCustomAttachmentList(context, customAttachmentMap, currentPage);

		this.attachmentList = new Gson().toJson(customAttachmentList);
		this.pagination = new Gson().toJson(this.getPaginationMap(currentPage));
		long stopTime = System.nanoTime();
		System.out.println("AttachmentAction DoGet: ");
		System.out.println(stopTime - startTime);
		return SUCCESS;
	}

	@SuppressWarnings("ConstantConditions")
	private List<CustomAttachment> getFilteredCustomAttachmentList(ActionContext context,
			Map<String, CustomAttachment> customAttachmentMap, int currentPage) {
		boolean isNameChecked = getFirstCheckBoxParameterValueAsBoolean(context, "name");
		String nameQuery = getFirstParameterValueAsString(context, "nameQuery");
		boolean isAuthorChecked = getFirstCheckBoxParameterValueAsBoolean(context, "author");
		String authorQuery = getFirstParameterValueAsString(context, "authorQuery");
		boolean isUsageChecked = getFirstCheckBoxParameterValueAsBoolean(context, "usage");
		String usageQuery = getFirstParameterValueAsString(context, "usageQuery");
		boolean isCreatedDateChecked = getFirstCheckBoxParameterValueAsBoolean(context, "createdDateCheckbox");
		boolean isUploadDateChecked = getFirstCheckBoxParameterValueAsBoolean(context, "uploadDateCheckbox");
		Date createdFromDate = getDateParameter(context, "createdFromDate");
		Date createdToDate = getDateParameter(context, "createdToDate");
		Date uploadFromDate = getDateParameter(context, "uploadFromDate");
		Date uploadToDate = getDateParameter(context, "uploadToDate");

		this.cacheFilters(isNameChecked, nameQuery, isAuthorChecked, authorQuery, isUsageChecked, usageQuery,
				isCreatedDateChecked, isUploadDateChecked, createdFromDate, createdToDate, uploadFromDate,
				uploadToDate);

		boolean allAttachment = getFirstCheckBoxParameterValueAsBoolean(context, "allAttachment");
		if (allAttachment == true) {
			return customAttachmentMap.values().parallelStream().filter(attachment -> {
				boolean namePass = isNameChecked
						&& attachment.getTitle().toLowerCase().contains(nameQuery.toLowerCase());
				boolean authorPass = isAuthorChecked
						&& attachment.getCreatorName().toLowerCase().contains(authorQuery.toLowerCase());
				boolean usagePass = isUsageChecked
						&& matchAnyPageTitle(usageQuery.toLowerCase(), attachment.getId().toString());
				boolean createdDatePass = isCreatedDateChecked
						&& isDateInBetweenOrEqual(attachment.getFileCreatedDate(), createdFromDate, createdToDate);
				boolean uploadDatePass = isUploadDateChecked
						&& isDateInBetweenOrEqual(attachment.getCreatedDate(), uploadFromDate, uploadToDate);

				return isNameChecked == namePass && isAuthorChecked == authorPass && isUsageChecked == usagePass
						&& isCreatedDateChecked == createdDatePass && isUploadDateChecked == uploadDatePass;
			}).sorted(this.attachmentComparator()).collect(Collectors.toList());
		}

		int startIndex = (currentPage - 1) * PAGE_SIZE;
		return customAttachmentMap.values().parallelStream().filter(attachment -> {
			boolean namePass = isNameChecked && attachment.getTitle().toLowerCase().contains(nameQuery.toLowerCase());
			boolean authorPass = isAuthorChecked
					&& attachment.getCreatorName().toLowerCase().contains(authorQuery.toLowerCase());
			boolean usagePass = isUsageChecked
					&& matchAnyPageTitle(usageQuery.toLowerCase(), attachment.getId().toString());
			boolean createdDatePass = isCreatedDateChecked
					&& isDateInBetweenOrEqual(attachment.getFileCreatedDate(), createdFromDate, createdToDate);
			boolean uploadDatePass = isUploadDateChecked
					&& isDateInBetweenOrEqual(attachment.getCreatedDate(), uploadFromDate, uploadToDate);

			return isNameChecked == namePass && isAuthorChecked == authorPass && isUsageChecked == usagePass
					&& isCreatedDateChecked == createdDatePass && isUploadDateChecked == uploadDatePass;
		}).sorted(this.attachmentComparator()).skip(startIndex).limit(PAGE_SIZE).collect(Collectors.toList());
	}

	private void cacheFilters(boolean isNameChecked, String nameQuery, boolean isAuthorChecked, String authorQuery,
			boolean isUsageChecked, String usageQuery, boolean isCreatedDateChecked, boolean isUploadDateChecked,
			Date createdFromDate, Date createdToDate, Date uploadFromDate, Date uploadToDate) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("hasFilter", true);
		map.put("name", isNameChecked);
		map.put("nameQuery", nameQuery);
		map.put("author", isAuthorChecked);
		map.put("authorQuery", authorQuery);
		map.put("usage", isUsageChecked);
		map.put("usageQuery", usageQuery);
		map.put("createdDateCheckbox", isCreatedDateChecked);
		map.put("uploadDateCheckbox", isUploadDateChecked);
		map.put("createdFromDate", createdFromDate);
		map.put("createdToDate", createdToDate);
		map.put("uploadFromDate", uploadFromDate);
		map.put("uploadToDate", uploadToDate);
		this.filters = new Gson().toJson(map);
	}

	private List<CustomAttachment> getCustomAttachmentList(ActionContext context,
			Map<String, CustomAttachment> customAttachmentMap, int currentPage) {
		int startIndex = (currentPage - 1) * PAGE_SIZE;
		boolean allAttachment = getFirstCheckBoxParameterValueAsBoolean(context, "allAttachment");
		if (allAttachment) {
			return customAttachmentMap.values().parallelStream().sorted(this.attachmentComparator())
					.collect(Collectors.toList());
		}
		return customAttachmentMap.values().parallelStream().sorted(this.attachmentComparator()).skip(startIndex)
				.limit(PAGE_SIZE).collect(Collectors.toList());
	}

	private HashMap<String, Object> getPaginationMap(int currentPage) {
		HashMap<String, Object> paginationMap = new HashMap<>();

		int totalPage = (int) Math.ceil(this.attachmentCount / PAGE_SIZE);

		paginationMap.put("totalPage", totalPage);
		paginationMap.put("currentPage", currentPage);
		return paginationMap;
	}

	

	@SuppressWarnings("DuplicatedCode")
	public Boolean isDateInBetweenOrEqual(Date input, Date start, Date end) {
		if (input == null) {
			return false;
		}
		if (start == null && end == null) {
			return true;
		} else if (start == null) {
			return input.compareTo(end) <= 0;
		} else if (end == null) {
			return input.compareTo(start) >= 0;
		} else {
			return input.compareTo(start) >= 0 && input.compareTo(end) <= 0;
		}
	}

	private Comparator<CustomAttachment> attachmentComparator() {
		return (a1, a2) -> {
			if (a1 == null) {
				return -1;
			}

			if (a2 == null) {
				return 1;
			}

			int result = 0;
			for (String sortType : this.sortByMulti) {
				switch (sortType) {
				case "fileCreatedDate":
					result = dateCompareNullFirst(a1.getFileCreatedDate(), a2.getFileCreatedDate());
					break;
				case "lastModifydate":
					result = dateCompareNullFirst(a1.getLastModifydate(), a2.getLastModifydate());
					break;
				case "size":
					result = Long.compare(a1.getFileSize(), a2.getFileSize());
					break;
				case "createdDate":
					result = dateCompareNullFirst(a1.getCreatedDate(), a2.getCreatedDate());
					break;
				case "name":
				default:
					if (a1.getTitle() == null) {
						return -1;
					}

					if (a2.getTitle() == null) {
						return 1;
					}

					result = a1.getTitle().toLowerCase().compareTo(a2.getTitle().toLowerCase());
				}

				if (result != 0) {
					return result;
				}
			}

			return result;
		};
	}

	private int dateCompareNullFirst(Date a1, Date a2) {
		if (a1 == null)
			return (a2 == null) ? 0 : -1;
		else if (a2 == null)
			return 1;
		else
			return a1.compareTo(a2);
	}

	private Map<String, CustomAttachment> attachmentsToMap(List<Attachment> attachments, Boolean isAttachmentByLang,
			String langKey, boolean isBaseLanguange) {
		String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
		HashMap<String, CustomAttachment> attachmentHashMap = new HashMap<>();
		try {
			for (Attachment attachment : attachments) {
				if (attachment.isDeleted() == true || attachment.isHidden() == true || attachment.isDraft() == true) {
					continue;
				}

				if (isAttachmentByLang == true) {
					boolean showAttachment = CommonFunction.checkShowAttachmentByLanguage(attachment, langKey,
							isBaseLanguange);
					if (showAttachment == false) {
						continue;
					}
				}
				Long originalVersionId = attachment.getOriginalVersionId();
				if (originalVersionId != null) { // child
					CustomAttachment customAttachment = attachmentHashMap.get(originalVersionId.toString());
					if (customAttachment == null) {
						continue;
					}
					customAttachment
							.prependHistoricalAttachment(new CustomAttachment(attachment, baseUrl, this.pageId));
					attachmentHashMap.put(originalVersionId.toString(), customAttachment);
				} else { // parent
					CustomAttachment customAttachment = attachmentHashMap.getOrDefault(attachment.getIdAsString(),
							new CustomAttachment(attachment, baseUrl, this.pageId));
					customAttachment.fromAttachment(attachment);
					attachmentHashMap.put(attachment.getIdAsString(), customAttachment);
					this.attachmentCount += 1;
				}
			}
		} catch (Exception e) {
			ManualLogUtil.outputLog("Exception attachmentsToMap : " + e.getMessage());
		}
		return attachmentHashMap;
	}

	private void initiateAttachmentUsageJob() {
		HashMap<String, Object> inputMap = new HashMap<>();
		inputMap.put("pageId", this.pageId);
		inputMap.put("spaceKey", this.spaceKey);

		AttachmentListPageCallback<String> callback = new AttachmentListPageCallback<>(inputMap);
		ManualVersionTask task = new ManualVersionTask(callback, inputMap);
		LongRunningTaskManager longRunningTaskManager = ManualVersionFactory.getLongRunningTaskManager();
		LongRunningTaskId longRunningTaskId = longRunningTaskManager.startLongRunningTask(getAuthenticatedUser(), task);

		this.taskId = longRunningTaskId.toString();
	}

	public PageManager getPageManager() {
		return pageManager;
	}

	public void setPageManager(PageManager pageManager) {
		this.pageManager = pageManager;
	}
}
