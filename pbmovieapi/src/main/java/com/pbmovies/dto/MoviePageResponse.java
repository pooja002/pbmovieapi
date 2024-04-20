package com.pbmovies.dto;

import java.util.List;

public record MoviePageResponse (List<MovieDTO> movieDTOList, Integer pageNumber, Integer pageSize, long totalElements,  int totalPages, boolean isLast){


}
