import apiClient from "@/request";

export const getSectionDetails = async (sectionId: string) => {
  return await apiClient.request({
    url: `/section/${sectionId}`,
    method: "GET",
  });
};
