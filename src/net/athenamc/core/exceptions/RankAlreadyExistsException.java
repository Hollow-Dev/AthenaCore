package net.athenamc.core.exceptions;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.athenamc.core.ranks.Rank;

@RequiredArgsConstructor
public class RankAlreadyExistsException extends Exception {
	private static final long serialVersionUID = 6437070891635598129L;
	
	@NonNull @Getter private Rank rank;
}
