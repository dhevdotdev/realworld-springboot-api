package dev.dhev.realworld.shared;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffsetPageRequest implements Pageable {

    private final int limit;
    private final long offset;
    private final Sort sort;

    public OffsetPageRequest(long offset, int limit) {
        this.offset = Math.max(0, offset);
        this.limit  = Math.max(1, limit);
        this.sort   = Sort.by(Sort.Direction.DESC, "createdAt");
    }

    @Override public int getPageNumber()           { return (int)(offset / limit); }
    @Override public int getPageSize()             { return limit; }
    @Override public long getOffset()              { return offset; }
    @Override public Sort getSort()                { return sort; }
    @Override public Pageable next()               { return new OffsetPageRequest(offset + limit, limit); }
    @Override public Pageable previousOrFirst()    { return new OffsetPageRequest(Math.max(0, offset - limit), limit); }
    @Override public Pageable first()              { return new OffsetPageRequest(0, limit); }
    @Override public Pageable withPage(int page)   { return new OffsetPageRequest((long) page * limit, limit); }
    @Override public boolean hasPrevious()         { return offset > 0; }
}
