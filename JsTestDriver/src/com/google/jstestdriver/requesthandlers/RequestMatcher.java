// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.requesthandlers;

import com.google.common.base.Objects;

/**
 * A matcher that matches prefix, suffix and literal servlet path-specs.
 *
 * e.g. "*.mp3", "/root/*" or "/javascript/source.js"
 *
 * Modelled after UriPatternMatcher in Guice Servlets.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
class RequestMatcher {

  private final HttpMethod method;
  private final String pattern;
  private final Kind kind;

  private enum Kind { PREFIX, SUFFIX, LITERAL }

  public RequestMatcher(HttpMethod method, String pattern) {
    this.method = method;
    if (pattern.startsWith("*")) {
      this.pattern = pattern.substring(1);
      this.kind = Kind.PREFIX;
    } else if (pattern.endsWith("*")) {
      this.pattern = pattern.substring(0, pattern.length() - 1);
      this.kind = Kind.SUFFIX;
    } else {
      this.pattern = pattern;
      this.kind = Kind.LITERAL;
    }
  }

  /**
   * @return true iff the method and uri match the method and pattern of this matcher.
   *
   * @param method an {@link HttpMethod}
   * @param uri the URI of the request
   */
  public boolean matches(HttpMethod method, String uri) {
    if (method == null || uri == null) {
      return false;
    }

    if (!method.equals(this.method)) {
      return false;
    }

    return uriMatches(uri);
  }

  /**
   * @return true iff the uri matches the pattern
   * 
   * @param uri the URI of the request
   */
  public boolean uriMatches(String uri) {
    switch (kind) {
      case PREFIX:
        return uri.endsWith(pattern);
      case SUFFIX:
        return uri.startsWith(pattern);
      default:
        return uri.equals(pattern);
    }
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(method, pattern, kind);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (!(other instanceof RequestMatcher)) {
      return false;
    }

    RequestMatcher that = (RequestMatcher) other;

    return this.method.equals(that.method)
        && this.pattern.equals(that.pattern)
        && this.kind.equals(that.kind);
  }
}
