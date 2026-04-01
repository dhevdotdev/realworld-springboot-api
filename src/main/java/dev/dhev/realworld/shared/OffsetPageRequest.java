package dev.dhev.realworld.shared;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

public class OffsetPageRequest implements Pageable {

    private final int limit;
    private final long offset;
    private final Sort sort;

    public OffsetPageRequest(long offset, int limit) {
        this.offset = Math.max(0, offset);
        this.limit  = Math.max(1, limit);
        this.sort   = Sort.by(Sort.Direction.DESC, "createdAt");
    }

    @Override public int getPageNumber()                { return (int)(offset / limit); }
    @Override public int getPageSize()                  { return limit; }
    @Override public long getOffset()                   { return offset; }
    @Override @NonNull public Sort getSort()            { return sort; }
    @Override @NonNull public Pageable next()           { return new OffsetPageRequest(offset + limit, limit); }
    @Override @NonNull public Pageable previousOrFirst(){ return new OffsetPageRequest(Math.max(0, offset - limit), limit); }
    @Override @NonNull public Pageable first()          { return new OffsetPageRequest(0, limit); }
    @Override @NonNull public Pageable withPage(int p)  { return new OffsetPageRequest((long) p * limit, limit); }
    @Override public boolean hasPrevious()              { return offset > 0; }
}
