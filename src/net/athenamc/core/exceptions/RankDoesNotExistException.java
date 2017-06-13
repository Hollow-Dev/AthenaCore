package net.athenamc.core.exceptions;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RankDoesNotExistException extends Exception {
	private static final long serialVersionUID = -7288651670462178962L;
	@NonNull @Getter	private String rankName;
}
