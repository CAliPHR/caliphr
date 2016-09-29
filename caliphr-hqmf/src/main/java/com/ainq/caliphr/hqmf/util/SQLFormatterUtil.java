/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package com.ainq.caliphr.hqmf.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.hibernate.internal.util.StringHelper;

/**
 * Performs formatting of basic SQL statements (DML + query).
 * 
 * **** Heavily modified from the BasicFormatterImpl class in the Hibernate project and  **** 
 * **** customized to a format that makes more sense for use in Caliphr                  ****
 * 
 * @author Daniel Rosenbaum
 *
 * @author Gavin King
 * @author Steve Ebersole
 */
public class SQLFormatterUtil {

	private static final Set<String> BEGIN_CLAUSES = new HashSet<String>();
	private static final Set<String> END_CLAUSES = new HashSet<String>();
	private static final Set<String> LOGICAL = new HashSet<String>();
	private static final Set<String> QUANTIFIERS = new HashSet<String>();
	private static final Set<String> MISC = new HashSet<String>();

	static {
		BEGIN_CLAUSES.add( "inner" );
		BEGIN_CLAUSES.add( "outer" );
		BEGIN_CLAUSES.add( "group" );
		BEGIN_CLAUSES.add( "order" );

		END_CLAUSES.add( "select" );
		END_CLAUSES.add( "left" );
		END_CLAUSES.add( "right" );
		END_CLAUSES.add( "where" );
		END_CLAUSES.add( "set" );
		END_CLAUSES.add( "having" );
		END_CLAUSES.add( "join" );
		END_CLAUSES.add( "from" );
		//END_CLAUSES.add( "by" );
		END_CLAUSES.add( "join" );
		END_CLAUSES.add( "into" );
		END_CLAUSES.add( "union" );
		END_CLAUSES.add( "intersect" );
		END_CLAUSES.add( "minus" );
		END_CLAUSES.add( "except" );

		LOGICAL.add( "and" );
		LOGICAL.add( "or" );
		LOGICAL.add( "when" );
		LOGICAL.add( "else" );
		LOGICAL.add( "end" );

		QUANTIFIERS.add( "in" );
		QUANTIFIERS.add( "all" );
		QUANTIFIERS.add( "exists" );
		QUANTIFIERS.add( "some" );
		QUANTIFIERS.add( "any" );

		MISC.add( "select" );
		MISC.add( "on" );
	}

	private static final String INDENT_STRING = "    ";

	public static String format(String source) {
		return new FormatProcess( source ).perform();
	}

	private static class FormatProcess {
		boolean beginLine = true;
		boolean afterPartition;
		Deque<Boolean> parensInFunctionStack = new ArrayDeque<Boolean>();

		int indent = 0;

		StringBuilder result = new StringBuilder();
		StringTokenizer tokens;
		String lastToken;
		String token;
		String lcToken;
		
		boolean wasLineBeginningAtLastSlash = false;

		public FormatProcess(String sql) {
			tokens = new StringTokenizer(
					sql,
					"()+*/-=<>'`\"[]," + StringHelper.WHITESPACE,
					true
			);
		}

		public String perform() {

			while ( tokens.hasMoreTokens() ) {
				token = tokens.nextToken();
				lcToken = token.toLowerCase();

				if ( "'".equals( token ) ) {
					String t;
					do {
						t = tokens.nextToken();
						token += t;
					}
					// cannot handle single quotes
					while ( !"'".equals( t ) && tokens.hasMoreTokens() );
				}
				else if ( "\"".equals( token ) ) {
					String t;
					do {
						t = tokens.nextToken();
						token += t;
					}
					while ( !"\"".equals( t ) );
				}

				//else 
				if ( "(".equals( token ) ) {
					openParen();
				}
				else if ( ")".equals( token ) ) {
					closeParen();
				}

				// special case for order if in a partition statement
				else if ( BEGIN_CLAUSES.contains( lcToken ) && !(afterPartition && "order".equals(lcToken))) {
					beginNewClause();
					afterPartition = false;
				}

				else if ( END_CLAUSES.contains( lcToken ) && 
					!(
						("join".equals(lcToken) && "outer".equals(lastToken)) ||
						("join".equals(lcToken) && "left".equals(lastToken)) ||
						("join".equals(lcToken) && "right".equals(lastToken))
					)) {
					endNewClause();
				}

				else if ( LOGICAL.contains( lcToken ) ) {
					logical();
				}

				else if ( isWhitespace( token ) ) {
					white();
				}
				
				// special case
				else if ( "row_number".equals(lcToken)) {
					newline();
					out();
					beginLine = false;
				}
				
				// special case: newline after a /* ... */ comment block
				else if ( "/".equals(lcToken) && "*".equals(lastToken)) {
						out();
						newline();
						beginLine = true;
					}
				
				// special case: newline before a /* ... */ comment block
				else if ("*".equals(lcToken) && "/".equals(lastToken)) {
					
					// remove the last character (the '/') to place a newline before it
					result.setLength(result.length() - 1);
					if (!wasLineBeginningAtLastSlash) {
						newline();
					}
					result.append( "/" );
					out();
					beginLine = false;
				}

				else {
					if ("/".equals(lcToken)) {
						wasLineBeginningAtLastSlash = beginLine;
					}
					misc();
				}

				if ( !isWhitespace( token ) ) {
					lastToken = lcToken;
				}
				
				if ("partition".equals(lcToken)) {
					afterPartition = true;
				}

			}
			return result.toString();
		}

		private void logical() {
			if ( "end".equals( lcToken ) ) {
				indent--;
			}
			newline();
			out();
			beginLine = false;
		}

		private void misc() {
			out();
			beginLine = false;
			if ( "case".equals( lcToken ) ) {
				indent++;
			}
		}

		private void white() {
			if ( !beginLine ) {
				result.append( " " );
			}
		}
		
		private void out() {
			result.append( token );
		}

		private void endNewClause() {
			if (!beginLine) {
				newline();
			}
			out();
			beginLine = false;
			if ("where".equals(lcToken)) {
				newline();
				beginLine = true;
			} 
		}

		private void beginNewClause() {
			out();
			beginLine = false;
		}

		private void closeParen() {
			boolean lastInFunction = parensInFunctionStack.pop();
			if (lastInFunction) {
				out();
			}
			else {
				indent--;
				newline();
				out();
			}
			beginLine = false;
		}

		private void openParen() {
			if (isFunctionName( lastToken )) {
				parensInFunctionStack.push(true);
				out();
				return;
			}
			if (!beginLine) {
				newline();
			}
			out();
			indent++;
			newline();
			parensInFunctionStack.push(false);
			beginLine = true;
			
		}

		private static boolean isFunctionName(String tok) {
			if(tok == null || "".equals(tok)) return false;
			final char begin = tok.charAt( 0 );
			final boolean isIdentifier = Character.isJavaIdentifierStart( begin ) || '"' == begin;
			return isIdentifier &&
					!LOGICAL.contains( tok ) &&
					!END_CLAUSES.contains( tok ) &&
					!QUANTIFIERS.contains( tok ) &&
					!MISC.contains( tok );
		}

		private static boolean isWhitespace(String token) {
			return StringHelper.WHITESPACE.contains( token );
		}

		private void newline() {
			result.append( "\n" );
			for ( int i = 0; i < indent; i++ ) {
				result.append( INDENT_STRING );
			}
			beginLine = true;
		}
	}
	
}

