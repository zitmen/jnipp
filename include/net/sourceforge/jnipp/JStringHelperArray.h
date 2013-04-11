#ifndef __net_sourceforge_jnipp_JStringHelperArray_H
#define __net_sourceforge_jnipp_JStringHelperArray_H

#ifdef WIN32
#pragma warning(disable:4251)
#pragma warning(disable:4786)
#endif

#include <vector>
#include <string>
#include "JNIEnvHelper.h"
#include "JStringHelper.h"

namespace net
{
	namespace sourceforge
	{
		namespace jnipp
		{
			template<unsigned int dims>
			class ComputeStringElementType
			{
			public:
				static inline std::string getElementTypeString()
				{
					return "[" + ComputeStringElementType<dims - 1>::getElementTypeString();
				}
			};

			template<>
			class ComputeStringElementType<1>
			{
			public:
				static inline std::string getElementTypeString()
				{
					return "java/lang/Object;";
				}
			};

			template<unsigned int dims>
			class JStringHelperArray
			{
				private:
					jobjectArray nativeArray;
					jsize length;

				public:
					JStringHelperArray()
						: nativeArray( NULL ), length( 0 )
					{
					}
					
					JStringHelperArray(jsize length)
					{
						this->length = length;
						jclass cls = JNIEnvHelper::FindClass( ComputeStringElementType<dims>::getElementTypeString().c_str() );
						nativeArray = JNIEnvHelper::NewObjectArray( length, cls, NULL );
					}
					
					JStringHelperArray(const jobject nativeArray)
					{
						this->nativeArray = reinterpret_cast<jobjectArray>( nativeArray );
						length = JNIEnvHelper::GetArrayLength( this->nativeArray );
					}

					JStringHelperArray(const JStringHelperArray<dims>& rhs)
					{
						*this = rhs;
					}

					JStringHelperArray<dims>& operator=(const JStringHelperArray<dims>& rhs)
					{
						this->nativeArray = rhs.nativeArray;
						this->length = rhs.length;
						return *this;
					}
					
					~JStringHelperArray()
					{
					}

					JStringHelperArray<dims - 1> getElementAt(jsize index) const
					{
						if ( index >= length )
							throw ArrayIndexOutOfBoundsException( index );

						return JNIEnvHelper::GetObjectArrayElement( nativeArray, index );
					}

					void setElementAt(jsize index, const JStringHelperArray<dims - 1>& value)
					{
						if ( index >= length )
							throw ArrayIndexOutOfBoundsException( index );
							
						JNIEnvHelper::SetObjectArrayElement( nativeArray, index, static_cast<jobject>(value) );
					}

					jsize getLength() const
					{
						return length;
					}
					
					operator jobjectArray() const
					{
						return nativeArray;
					}
					
					operator jobject() const
					{
						return nativeArray;
					}
			};
			
			class JStringHelperArray<1>
			{
			private:
				std::vector<JStringHelper> stringVector;
				
			public:
				JStringHelperArray()
				{
				}
				
				JStringHelperArray(jobject obj)
				{
					jobjectArray arr = reinterpret_cast<jobjectArray>( obj );
					
					jsize numElems = JNIEnvHelper::GetArrayLength( arr );

					for ( int i = 0; i < numElems; ++i )
						stringVector.push_back( JStringHelper( JNIEnvHelper::GetObjectArrayElement( arr, i ) ) );
				}

				JStringHelperArray(const std::vector<std::string>& strVector)
				{
					std::vector<std::string>::const_iterator it = strVector.begin();
					for ( ; it != strVector.end(); ++it )
						stringVector.push_back( it->c_str() );
				}
				
				JStringHelperArray(int argc, char **args)
				{
					for ( int i = 0; i < argc; ++i )
						stringVector.push_back( static_cast<const char *>(args[i]) );
				}
				
				~JStringHelperArray()
				{
				}
				
				void addElement(const JStringHelper& newString)
				{
					stringVector.push_back( newString );
				}
				
				void addElement(const jstring newString)
				{
					stringVector.push_back( newString );
				}
				
				JStringHelperArray& operator=(JStringHelperArray& rhs)
				{
					stringVector.clear();
					
					for ( std::vector<JStringHelper>::iterator it = rhs.stringVector.begin(); it != rhs.stringVector.end(); ++it )
						stringVector.push_back( *it );

					return *this;
				}
				
				JStringHelperArray& operator=(const jobjectArray rhs)
				{
					jsize numElems = JNIEnvHelper::GetArrayLength( rhs );

					for ( int i = 0; i < numElems; ++i )
						stringVector.push_back( JNIEnvHelper::GetObjectArrayElement( rhs, i ) );

					return *this;
				}
				
				operator jobjectArray() const
				{
					jobjectArray retVal;
					jsize numElems = stringVector.size();

					if ( numElems == 0 )
						return NULL;

					// create new array, all NULLs by default
					retVal = JNIEnvHelper::NewObjectArray( numElems, JStringHelper::getClass(), NULL );

					// copy the elements of the vector into the newly-created array
					jsize index = 0;
					for ( std::vector<JStringHelper>::const_iterator it = stringVector.begin(); it != stringVector.end(); ++it, ++index )
						JNIEnvHelper::SetObjectArrayElement( retVal, index, *it );

					return retVal;
				}

				operator jobject() const
				{
					return reinterpret_cast<jobject>( static_cast<jobjectArray>(*this) );
				}
			};
		}
	}
}

#endif
